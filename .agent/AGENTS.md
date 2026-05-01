# Repository Guidelines

## 프로젝트 개요

실시간 좌석 예매를 도입한 티켓팅 서비스입니다.

## 프로젝트 스펙

* Java 17
* Spring Boot 3.5.4
* MariaDB 3.5.4
* spring-boot-starter-data-redis-3.5.4
* JPA
* QueryDSL

## 프로젝트 구조

* 애플리케이션 코드는 `src/main/java/com/picketlogia/picket` 아래에 있으며, `api/user`, `api/product`, `api/reservation`처럼 기능 단위 패키지로 구성됩니다.
* 공통 인프라는 `config`, `common`, `utils`, `websocket`에 위치합니다. 
* 실행 설정과 SQL 리소스는 `src/main/resources`에 있으며 `application.yml`, `db/data.sql`, Redis Lua 스크립트를 포함합니다. 
* 테스트 코드는 `src/test/java`에 있고 대상 패키지 구조를 따라갑니다. 
* QueryDSL 생성 코드는 `src/main/generated`에 커밋되어 있으므로 직접 수정하지 않습니다.

## 빌드, 테스트, 개발 명령어

- `./gradlew bootRun`: 현재 Spring 설정으로 로컬 서버를 실행합니다.
- `./gradlew test`: JUnit 5 테스트를 실행합니다.
- `./gradlew clean build`: 컴파일, 테스트, 패키징을 한 번에 수행합니다.
- `./gradlew clean`: 빌드 산출물을 삭제해 생성 리소스가 꼬였을 때 초기화합니다.

시스템 Gradle 대신 저장소에 포함된 Gradle Wrapper를 사용하세요.

## 코딩 스타일 및 네이밍 규칙

* 들여쓰기는 공백 4칸을 사용합니다. 
* Java 기본 규칙을 따르며 클래스는 `PascalCase`, 메서드와 필드는 `camelCase`, 상수는 `UPPER_SNAKE_CASE`를 사용합니다. 
* Spring 구성요소는 `Controller`, `Service`, `Repository`처럼 역할이 드러나는 suffix를 유지하세요. 
* DTO는 `ReservationRegister`, `PaymentStatusResponse`처럼 목적이 드러나는 이름을 사용합니다. 
* 기능 로직은 기존 패키지 구조 안에 두고, 공통 예외 처리는 `common/exception`에 맞춥니다.
* `api/**`아래에 `controller`, `service`, `repository`, `model`, `dto` 구조로 구성됩니다.
* 서비스 레이어의 메서드들은 내부 로직에 맞는 올바른 비즈니스 메서드 이름을 사용합니다.
* 리포지토리 레이어의 메서드들은 리포지토리의 특성임을 알 수 있도록 파라미터의 조건들을 메서드명에 붙여줍니다.
* 메서드에는 javadoc을 준수하여 작성합니다.

## 코드 설계 구조

* 서비스가 다른 서비스를 참조(AutoWired)해야한다면 참조되는 서비스는 무조건 단일 규칙 도메인 서비스여야합니다.
* 각 클래스의 메서드들은 하나의 책임만을 가지도록 설계합니다.
* List, Set 등 컬렉션 객체들은 해당 객체로 어떤 행위를 수행해야한다면 일급객체로의 전환을 고려합니다.
* 발생할 수 있는 모든 예외를 체크합니다.

## 테스트 가이드

* JUnit 5, Spring Boot Test, AssertJ, Mockito를 사용합니다. 
* 테스트 클래스명은 `*Test` 형식을 따르고, 대상 코드와 같은 패키지 경로에 배치하세요. 
* 서비스 로직은 Mockito 기반 단위 테스트를 우선하고, 통합 확인이 필요한 경우에만 `@SpringBootTest`를 사용합니다.
* 단위테스트 시에는 DB서버와 독립된 테스트 환경을 구성할수 있도록 하세요.
* 테스트 코드 PR 전에 `./gradlew test`를 실행하세요. 별도 커버리지 게이트는 없으므로, 변경한 비즈니스 로직과 예외 흐름은 직접 검증해야 합니다.

## 커밋 및 Pull Request 가이드

* 최근 커밋은 `feat: `, `refactor: `, `fix: `, `chore ` 같은 접두사와 짧은 한국어 설명을 함께 사용합니다.
* 커밋의 접두사 선택은 해당 커밋 코드들을 보고 추론하여 선택하세요.
* 한 커밋에는 한 가지 변경만 담는 것이 좋습니다. 
* PR에는 변경 요약, 연결된 이슈 또는 작업 번호, 테스트 근거, 필요한 설정 또는 스키마 변경 사항을 포함하세요. 
* 문서 변경이나 외부 동작 변화가 있을 때만 스크린샷을 첨부하면 됩니다.

## 보안 및 설정 주의사항

`src/main/resources/application.yml`은 `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, AWS 키, 메일 계정, PortOne 비밀값 등 여러 환경변수를 전제로 합니다. 
실제 자격 증명, 로컬 비밀 파일, 운영용 설정값은 저장소에 커밋하지 마세요.

## plan 수행 작업

* plan이 생성되면 설계 이유를 md파일로 자세히 서술하세요.
* 최종 plan 종료 전에 항상 커밋 메시지를 작성해서 보여주세요.