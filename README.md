# 🎟️ Picket

<p align="center">
  <img src="./docs/picket_logo.png" width="300" alt="Picket 로고" />

# 팀 Picketnologia
> 한화시스템 BEYOND 17기 1팀 Picketnologia 미니 프로젝트 <br>
> 개발 기간 : 2025.07 ~ 2025.09

# 프로젝트 주소

[프로젝트 바로가기 - www.picket.o-r.kr](https://www.picket.o-r.kr)

> 일반 사용자 <br>
> test01@test.com <br>
> test03@test.com <br>
> <br>
> 판매자 <br>
> test02@test.com
>
> <br>
> 비밀번호는 모두 qwer1234 입니다.

# 프로젝트 소개

Picket은 공연 예매 플랫폼으로서 최근 공연, 전시 스포츠 이벤트에 대한 관심이 높아지면서 기존 플랫폼들의 많은 수요에도 불구하고 실시간성이 부족한 좌석 예매의 아쉬움을 해결하기 위하여 만들어졌습니다.

Picket에서는 실시간 좌석 기능 제공으로 원활한 좌석 예매 경험을 제공합니다.

## 기술 스택

### Front-end
![Vue.js](https://img.shields.io/badge/VUE.JS-4FC08D?style=for-the-badge&logo=vuedotjs&logoColor=white)

### Back-end
![Spring Boot](https://img.shields.io/badge/SPRING_BOOT-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)

### DB
![MariaDB](https://img.shields.io/badge/MARIADB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Redis](https://img.shields.io/badge/REDIS-DC382D?style=for-the-badge&logo=redis&logoColor=white)

### DevOps / Infra

![Jenkins](https://img.shields.io/badge/JENKINS-D24939?style=for-the-badge&logo=jenkins&logoColor=white)
![Docker](https://img.shields.io/badge/DOCKER-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/KUBERNETES-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)


## ERD

![ERD.png](docs/ERD.png)

## AWS 배포 아키텍처

![실제 배포 아키텍처.png](docs/%EC%8B%A4%EC%A0%9C%20%EB%B0%B0%ED%8F%AC%20%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98.png)

## CI / CD 아키텍처

![CI-CD 아키텍처.png](docs/CI-CD%20%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98.png)

## 주요 기능

### 회원가입

### 로그인

### 비밀번호 찾기

### 실시간 좌석 예매 동시성 제어

공연 예매 시 다수의 사용자가 동시에 좌석 예매 화면에서 실시간으로 확인 할 수 있습니다.

![ Concurrency Control2.gif](docs/%EA%B8%B0%EB%8A%A5%20%EB%8F%99%EC%9E%91/%20Concurrency%20Control2.gif)

### 결제

좌석을 선택하고 다음 단계를 진행하면 결제를 진행 할 수 있습니다.

![buyTicket.gif](docs/%EA%B8%B0%EB%8A%A5%20%EB%8F%99%EC%9E%91/buyTicket.gif)

