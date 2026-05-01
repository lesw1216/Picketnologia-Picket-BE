# 실시간 좌석 오픈 10분 전 선적재 계획

## 요약
- 기준 시각은 `Product.openDate`를 사용한다.
- 오픈 10분 전에 해당 공연의 모든 회차 좌석 데이터를 Redis에 선적재한다.
- 적재 대상은 `SeatInfo` 조회용 캐시와 실시간 잠금용 Redis 구조 둘 다 포함한다.
- cache-aside는 쓰지 않고, 사전 적재된 캐시를 우선 사용한다.

## 현재 구조 기준 해석
- 좌석 메타 조회는 `/seat-info`에서 DB 기반으로 생성된다.
- 실시간 잠금 상태는 `seat-status` Redis hash와 개별 좌석 TTL key로 관리된다.
- 회차별 오픈 시각은 없고, 현재 오픈 기준은 `Product.openDate` 하나다.
- 따라서 선적재 단위는 “공연 단위 오픈 시각에 맞춰 해당 공연의 모든 회차”로 잡는다.

## 구현 방향
### 1. 캐시 키 설계
- `seat-info:{roundTimeIdx}`
  - 좌석 등급, 좌석 맵, 예매 완료 좌석 반영 결과를 JSON으로 저장한다.
- `seat-status:{roundTimeIdx}`
  - 실시간 잠금 상태용 hash로 사용한다.
- 신규 키는 공백 없는 형식으로 통일한다.

### 2. 선적재 대상 조회
- `openDate`가 `now ~ now+10분` 범위인 공연을 조회한다.
- 각 공연에 연결된 모든 `RoundDate`, `RoundTime`을 조회한다.
- 회차별로 좌석 맵과 예약 완료 좌석을 조합해 `seat-info` 스냅샷을 만든다.
- 이미 캐시된 회차는 건너뛰어 idempotent하게 동작하게 한다.

### 3. 스케줄러
- Spring Scheduling을 활성화한다.
- 1분 주기 preload job을 둔다.
- blue/green 배포로 순간 중복 실행 가능성이 있으므로 다음 중 하나를 둔다.
  - preload lock key
  - 캐시 존재 시 skip
- 일부 회차 preload 실패는 전체 실패로 보지 않고 로그 후 다음 주기에 재시도한다.

### 4. 조회/실시간 반영
- `/seat-info`는 먼저 `seat-info:{roundTimeIdx}`를 조회한다.
- 캐시 hit면 그대로 반환한다.
- 캐시 miss면 preload 실패로 보고 로그를 남긴 뒤 DB fallback을 허용한다.
- WebSocket 좌석 선택/해제 흐름은 기존처럼 `seat-status:{roundTimeIdx}`를 갱신한다.
- 결제 완료 후에는 해당 회차 `seat-info`를 재생성하거나 무효화해야 stale 데이터가 남지 않는다.

### 5. TTL 정책
- `seat-info:{roundTimeIdx}`는 회차 종료 이후 짧은 버퍼를 두고 만료시킨다.
- `seat-status:{roundTimeIdx}`도 회차 종료 이후 자동 만료되게 둔다.
- 개별 좌석 lock key인 `seat:{roundTimeIdx}-{seatIdx}`의 10분 TTL 정책은 유지한다.

## 필요한 내부 변경
- 오픈 임박 공연 조회용 repository/service 추가
- 회차별 좌석 스냅샷 생성 서비스 추가
- preload scheduler 추가
- 결제 완료 후 `seat-info` 캐시 갱신 또는 무효화 훅 추가

## 테스트 항목
- `openDate - 10분` 범위의 공연만 preload 대상인지 확인
- 한 공연의 모든 회차가 캐시되는지 확인
- 이미 캐시된 회차를 중복 적재하지 않는지 확인
- `/seat-info`가 캐시 hit 시 캐시 응답을 반환하는지 확인
- WebSocket 선택/해제가 `seat-status`에 반영되는지 확인
- 결제 완료 후 `seat-info` 캐시 stale 문제가 없는지 확인
- 일부 회차 preload 실패 시 나머지는 계속 처리되는지 확인

## 기본 가정
- 회차별 오픈 시각은 도입하지 않는다.
- `Product.openDate` 기준으로 해당 공연의 모든 회차를 선적재한다.
- 운영 중 다중 인스턴스가 잠시 공존할 수 있다고 가정한다.
- cache-aside는 사용하지 않되, preload 실패 시 제한적 DB fallback은 허용한다.
