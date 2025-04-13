

## 개발 인원

1인 프로젝트 (본인)

## 요약

- http 풀링을 이용하여 메시지,중고거래 시스템을 구현하였습니다.


 - 읽지 않은 알람을 5초마다 받아와서 렌더링을 하고, 기존 알람과 비교하여 차이점이 있는 경우에만
렌더링을 합니다.

- 카카오 openApi 주소를 기반으로 하여 로그인 상태일 경우 가까운 주소와 거래 정보를 매핑을 합니다.

- 게시판 기반으로 되어있어서 거래 정보를 쉽게 볼 수 있으며, 
메시지와 알람을 통하여 거래 상황을 확인 및 진행할 수 있습니다.

## 실행 화면 

- AWS가 아닌 로컬로 실행하였으며, 세션은 Chrome, Edge로 각각 잡아서 하였습니다

- 한눈에 보기 쉽게 브라우저 배율을 50%로 잡아서 테스트하였습니다.


### 로그인 테스트
![로그인테스트](https://github.com/user-attachments/assets/9520d605-36c3-4929-9b20-37ea62ddc2fe)

### 회원가입 테스트
![회원가입테스트](https://github.com/user-attachments/assets/5564e071-4033-427e-9360-d49af491ae24)


### 회원가입을 한 계정을 로그인 해보기
![로그인테스트2](https://github.com/user-attachments/assets/29ee5d77-aa92-4078-926a-d9ce99c0afc7)

### 채팅 테스트
![채팅테스트](https://github.com/user-attachments/assets/7e366248-dcc6-4519-b426-91a8c9831ad6)

### 거래거절 테스트
![거래거절테스트](https://github.com/user-attachments/assets/5d2a0c46-63ba-41ae-9f08-867eb814ac59)

### 거래수락 테스트
![거래수락테스트](https://github.com/user-attachments/assets/a8b67ecb-7b0b-4c38-ad87-27c8c1aa115c)

### 중복된 거래신청이 올 시 신청만료으로 전환됨을 확인
![신청만료테스트](https://github.com/user-attachments/assets/afeea907-77ff-4e0d-8647-28a384171440)

### 거래완료 테스트 1 (게시자 쪽에서 거래완료 신청을 보냄)
![거래완료테스트1](https://github.com/user-attachments/assets/723b3e02-98ce-419e-8c3b-6e76d56c09c6)

### 거래완료 테스트 2 (신청자 쪽에서 거래완료 신청을 확인한 후 이를 수락)
![거래완료테스트2](https://github.com/user-attachments/assets/b00635d4-c498-421e-b447-95895eecbec6)

### 댓글 추가 테스트
![댓글추가테스트](https://github.com/user-attachments/assets/15c6ef1e-e817-4abb-8c5b-6251fdf8684b)

### 댓글 비밀글,블라인드 테스트
![댓글부가기능테스트](https://github.com/user-attachments/assets/55615485-dba4-4a07-b774-a01bbbca450a)
![블라인드테스트](https://github.com/user-attachments/assets/1f7bf4ac-1675-4dd0-b0ab-2132d3606aca)

### 댓글 수정, 삭제 테스트
![수정테스트](https://github.com/user-attachments/assets/90d93ff0-f3f9-41b3-9908-c89c00410456)

![삭제테스트](https://github.com/user-attachments/assets/ce8baad2-b6f0-47e1-8aa7-4a9a461656ba)

### 게시글 삭제 테스트
![게시글삭제테스트](https://github.com/user-attachments/assets/c8cbcb37-6e15-4585-905b-434e89d7793c)

<!--### 게시글 삭제 후 이와 관련된 내용이 마이페이지의 목록에 있는지 확인 (거래완료된 거래는 예외처리)
![삭제후목록조회테스트](https://github.com/user-attachments/assets/7089dfff-3004-46dc-96a9-d26e8f1661cc)
-->

### 마이페이지 조회

![마이페이지조회(나의게시글,댓글)](https://github.com/user-attachments/assets/99e6407c-ee07-45f9-a6f2-ddcac98cfe81)
![마이페이지조회(거래목록,관심목록)](https://github.com/user-attachments/assets/4e5a0ef3-c758-48a1-8bf6-d16ef84343a3)

### 검색 테스트

![검색테스트](https://github.com/user-attachments/assets/671604dd-df2d-4c68-a10c-c0aa7c8ca6c9)

### 로그아웃 테스트
![로그아웃테스트](https://github.com/user-attachments/assets/b963bde8-69b6-40e8-9166-f086a2a94553)

### 알람 모두 읽기 기능 및 모두 삭제 
![알람기능테스트](https://github.com/user-attachments/assets/c5babe6a-d930-4c68-8e07-b1e0dd361ff6)


그 외, 아이디찾기, 비밀번호찾기,아이디저장, 중복아이디, 닉네임 확인, 알림을 통한 거래신청 및 거래완료, 알림 개별 선택 읽기, 알림 개별 삭제, 채팅방 모두 읽기 등 기능이 구현되어 있습니다

## Backend

Java 17 / Spring Boot 3.4.1

Spring Security (로그인 과정)

JPA (MySQL)



## Frontend

JavaScript

CSS

HTML5

Thymeleaf


## Upload 

https://github.com/Jinwook93/ReMarket-personal-project


## OS

 Windows 11

## Tools

 Spring Tools Suite, DBeaver

## Velog

 https://velog.io/@93jinucklee/series








