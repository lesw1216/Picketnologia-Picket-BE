# OpenAPI 단일 파일 추출

> 생성일: 2026-05-21
> 완료일: 2026-05-21
> 브랜치: docs/openapi-yaml-extract

## Tasks

- [x] auth / token / user 도메인 컨트롤러 스캔
- [x] seller / product / seat 도메인 컨트롤러 스캔
- [x] reservation / payments 도메인 컨트롤러 스캔
- [x] review / qna / contents 도메인 컨트롤러 스캔
- [x] genre / region / sortoption 도메인 컨트롤러 스캔
- [x] openapi.yaml info / servers / tags 작성
- [x] paths 섹션 작성 (37 paths / 44 operations)
- [x] components/schemas 작성 (40개 스키마, BaseResponse · ErrorResponse 포함)
- [x] yaml 문법·완전성 검증 (python yaml.safe_load 통과, 빈 summary 없음)
- [x] 빌드 확인 (`./gradlew build -x test` BUILD SUCCESSFUL)

## 참고

- 테스트(`./gradlew test`)는 사전 존재 이슈(DB 환경변수 미설정, 기존 Mockito 스터빙 경고)로 실패하나 본 작업의 yaml 추가와 무관함.
- 컨트롤러/DTO 어노테이션 보강은 후속 작업으로 분리.
