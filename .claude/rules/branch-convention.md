# 브랜치 컨벤션

## 브랜치 전략

```
main
 └── dev          ← 기본 개발 브랜치, 모든 작업 브랜치는 여기서 분기
       ├── feat/#12-{브랜치명}
       ├── fix/#15-{브랜치명}
       └── ...
```

- **dev**: 기본 개발 브랜치. 모든 작업 브랜치는 dev에서 생성하고 dev로 PR
- **main**: 배포 전용. dev → main merge 시 GitHub Actions로 자동 배포. 직접 커밋 금지

## 브랜치 네이밍

```
{타입}/#{이슈번호}-{브랜치명}
```

### 타입

| 타입 | 설명 |
|------|------|
| `feat` | 새로운 기능 |
| `fix` | 버그 수정 |
| `refactor` | 코드 개선 |
| `chore` | 빌드·설정·의존성 변경 |
| `docs` | 문서 작성·수정 |
| `test` | 테스트 코드 작성·수정 |

### 예시

```
feat/#12-product-registration
fix/#15-seat-status-bug
refactor/#20-dto-layer-cleanup
chore/#8-redis-setup
docs/#3-api-spec-update
test/#25-reservation-service-test
```

## 브랜치 생성 명령

```bash
# dev 기준으로 브랜치 생성
git checkout dev
git pull origin dev
git checkout -b feat/#12-product-registration
```
