# Seat 저장 책임 리팩토링 메모

## 배경

`SeatInfoService`는 이름상 좌석 조회 퍼사드에 가까운데, 실제로는 `save()`를 통해 좌석 등급 저장과 좌석 저장까지 함께 수행하고 있었다.

이 구조는 다음 문제를 만들었다.

- `SeatInfoService`가 조회와 저장 책임을 함께 가진다.
- `ProductService.register()`는 좌석 저장을 위임하지만, 어떤 성격의 위임인지 이름만 보면 바로 드러나지 않는다.
- 서비스가 다른 서비스를 참조할 때, 참조 당하는 서비스는 단일 규칙 도메인 서비스여야 한다는 설계 기준과 어긋난다.

## 고민한 선택지

### 1. `SeatInfoService.save()` 유지

장점:

- `ProductService.register()`가 간단해진다.

단점:

- 조회 전용처럼 보이는 서비스가 저장 오케스트레이션까지 맡는다.
- `SeatInfoService`의 이름과 실제 책임이 맞지 않는다.

### 2. `ProductService`가 `SeatRepository`를 직접 의존

장점:

- 중간 서비스가 줄어든다.

단점:

- `ProductService`가 좌석 등급 저장, 등급 IDX 매핑, 좌석 엔티티 변환, 좌석 저장 세부 구현까지 알게 된다.
- 상품 서비스가 좌석 저장 규칙까지 떠안게 되어 책임이 과해진다.

### 3. `ProductService`가 `SeatGradeService`, `SeatService`를 직접 조합

장점:

- 상위 유스케이스 서비스가 오케스트레이션을 담당한다.
- 하위 서비스는 각각 단일 규칙 도메인 서비스로 남는다.
- `SeatInfoService`를 조회 전용으로 정리할 수 있다.

단점:

- `ProductService.register()` 내부 단계가 한 줄 늘어난다.

## 최종 선택

3번을 선택했다.

설계 기준은 다음과 같다.

- 서비스가 다른 서비스를 참조할 때, 참조 당하는 서비스는 단일 규칙 도메인 서비스로 유지한다.
- 여러 단일 규칙 서비스를 엮는 오케스트레이션은 상위 유스케이스 서비스가 담당한다.
- 조회용 서비스와 저장용 흐름은 이름과 책임이 일치해야 한다.

따라서:

- `SeatInfoService`에서는 `save()`를 제거했다.
- `SeatInfoService`는 좌석 조회 전용 서비스로 유지한다.
- `ProductService.register()`가 좌석 저장 오케스트레이션을 직접 수행한다.
- `SeatGradeService`는 좌석 등급 저장과 grade-to-idx 매핑 반환만 담당한다.
- `SeatService`는 좌석 저장만 담당한다.

## 변경 후 흐름

`ProductService.register()` 내부 흐름:

1. 상품 저장
2. 회차 저장
3. `seatGradeService.saveAll(...)` 호출
4. 반환된 `seatGradeMap`으로 `seatService.saveAll(...)` 호출
5. 이미지 업로드

조회 흐름은 그대로 `SeatInfoService.findSeatInfo(...)`에 둔다.

## 기대 효과

- 서비스 이름과 책임이 더 잘 맞는다.
- `SeatInfoService`가 조회 퍼사드로 명확해진다.
- 좌석 저장 규칙이 `ProductService` 유스케이스 흐름 안에서 드러난다.
- 하위 서비스들은 단일 책임 서비스로 유지된다.
