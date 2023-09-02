# AccountProject
회원가입 후 계좌 관리(생성/해지/확인) 및 타 계좌로 이체하는 시스템

## ERD 설계
![image](https://github.com/kku1031/AccountProject/assets/106217267/0091134e-5b24-4002-9e42-103f2f596b48)


## 기술 스택
- 활용 DB : `H2 DB`(memory DB 모드), `MariaDB`
- DB접근방식 : `Spring data jpa`
- 보조서버 : `Embedded redis`
- API Request 및 Response : `json` 타입
- 인증 허가 : `Spring Security`, `jwt`

## 구현기능

✅ 회원가입 API
- 회원가입 시 아이디, 비밀번호, 이름, 이메일이 필요하다.
- 회원가입 시 이미 가입된 아이디로 가입을 시도하면 에러가 발생한다.
- 중복 아이디는 허용하지 않는다.
- 패스워드는 암호화된 형태로 DB에 저장한다.

✅ 로그인/로그아웃 API
- 로그인 시 아이디와 비밀번호는 일치해야 한다.
- 로그아웃 시 계좌/거래 서비스를 이용할 수 없다

✅ 계좌 관련 API

- 계좌 생성 
- 계좌 해지
- 계좌 확인

✅ 거래(입/출금) API
- 잔액 사용
- 잔액 사용 취소
- 거래 확인

✅ 송금 API
