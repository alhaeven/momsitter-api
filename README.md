
# Momsitter-api

Toy Project
<br>
Backend Api

- Language : Java 8
- Framework : Spring + security
- Auth Token library : jwt
- Build tool : Gradle
- DB : MariaDB on Docker
- DDL : [./sql/DB_DDL.sql](./sql/DB_DDL.sql)에 명시
- [OpenApi(Swagger)](localhost:8080/swagger-ui.html) 적용

## JWT Config
- application.yml
```yaml
jwt:
  secret: "복호화 키"
  access-token-expireTime: 600000 #10minute #token 유효시간 설정
```

## Authorization Request Header
- Key : Authorization
- Value : 'Bearer ' + jwt access token

## JSON Basic format
```json lines
{
  "userIdx" : "회원번호",
  "userId" : "id",
  "password" : "비밀번호 / 8자리 이상, 대,소문자,특수문자,숫자하나이상",
  "name" : "이름",
  "birth" : "생년월일 yyyyMMdd 형식",
  "gender" : "m:남자/ f:여자",
  "email" : "email 주소",
  "role" : "user/sitter/ ','로 연결하여 다중 role 사용 ex) user,sitter",
  "user" : {
    "reqInfo" : "요청사항",
    "children" : [
      {
        "childIdx" : "update시 사용",
        "birth" : "생년월일 yyyyMMdd 형식",
        "gender" : "m:남자/ f:여자"
      }
    ]
  },
  "sitter" : {
    "coverageMin" : 3, // 케어 가능한 최소연령 기본값 0
    "coverageMax" : 5, // 케어 가능한 최대연령 기본값 200
    "intro" : "자기 소개"
  }
}
```

## API definition
[Swagger-OpenApi](http://localhost:8080/swagger-ui.html)

- POST /api/account/join
- POST /api/account/login
- PUT /api/member/{userIdx}
- PATCH /api/member/{userIdx}
- GET /api/member/{userIdx}
- PUT /api/member/{userIdx}/child
- DELETE /api/member/{userIdx}/child/{childIdx}

### [POST] /api/account/join
- 회원가입
- 인증없이 접근 가능
- role 정보를 통해 유형 구분
- Request ; case role user(부모)
```json lines
{
  "userIdx" : "회원번호",
  "userId" : "id",
  "password" : "비밀번호 / 8자리 이상, 대,소문자,특수문자,숫자하나이상",
  "name" : "이름",
  "birth" : "생년월일 yyyyMMdd 형식",
  "gender" : "m:남자/ f:여자",
  "email" : "email 주소",
  "role" : "user",
  "user" : {
    "reqInfo" : "요청사항",
    "children" : [
      {
        "birth" : "생년월일 yyyyMMdd 형식",
        "gender" : "m:남자/ f:여자"
      }
    ]
  }
}
```
- Request ; case role sitter(시터)
```json lines
{
  "userIdx" : "회원번호",
  "userId" : "id",
  "password" : "비밀번호 / 8자리 이상, 대,소문자,특수문자,숫자하나이상",
  "name" : "이름",
  "birth" : "생년월일 yyyyMMdd 형식",
  "gender" : "m:남자/ f:여자",
  "email" : "email 주소",
  "role" : "sitter",
  "sitter" : {
    "coverageMin" : 3,
    "coverageMax" : 5,
    "intro" : "자기 소개"
  }
}
```
- 실패 Response
```json lines
id 중복
{
  "dateTime": "2023-06-22T21:00:00+09",
  "success": false,
  "response": "duplicate user id",
  "error": null
}
```
```json lines
Password 유효성 검사 실패
유효조건 - 대문자 소문자 숫자 특수문자 각 1자이상 포함한 8자리 이상
{
"dateTime": "2023-06-22T21:00:00+09",
"success": false,
"response": "invalid password",
"error": null
}
```
- 성공 Response
```json lines
{
"dateTime": "2023-06-22T21:00:00+09",
"success": true,
"response": "success join : joinedId",
"error": null
}
```

### [POST] /api/account/login
- 로그인
- 인증없이 접근 가능
- Request
```json lines
Body
{
  "userId" : "id",
  "password" : "password"
}
```
- 성공시 Resonse
```text
Header
Authrization : Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxd2VycnIiLCJhdXRoIjoic2l0dGVyLHVzZXIiLCJpYXQiOjE2ODc0MDEwMzMsImV4cCI6MTY4NzQwNzAzM30.DKpZj8KYS_SLC4Qu5to77KGQsdxCXSA0w2SGvsbjTmA
```
```json lines
Body
{
  "dateTime": "2023-06-22T21:00:00+09",
  "success": true,
  "response": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxd2VycnIiLCJhdXRoIjoic2l0dGVyLHVzZXIiLCJpYXQiOjE2ODc0MDEwMzMsImV4cCI6MTY4NzQwNzAzM30.DKpZj8KYS_SLC4Qu5to77KGQsdxCXSA0w2SGvsbjTmA"
  },
  "error": null
}
```
- 실패시 Response
```json lines
{
  "dateTime": "2023-06-22T21:00:00+09",
  "success": false,
  "response": null,
  "error":{
    "code": "auth",
    "message": "UNAUTHORIZED",
    "status": 401
  }
}
```

### [PUT] /api/member/{userIdx}
- 역할 추가
- 인증 Token 필요
- Request
```text
Header
Authrization : Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxd2VycnIiLCJhdXRoIjoic2l0dGVyLHVzZXIiLCJpYXQiOjE2ODc0MDEwMzMsImV4cCI6MTY4NzQwNzAzM30.DKpZj8KYS_SLC4Qu5to77KGQsdxCXSA0w2SGvsbjTmA
```
```json lines
{
  "role" : "추가하고자 하는 역할 명시", // user | sitter
  "user" : {
    "reqInfo" : "요청사항",
    "children" : [
      {
        "birth" : "생년월일 yyyyMMdd 형식",
        "gender" : "m:남자/ f:여자"
      }
    ]
  },
  "sitter" : {
    "coverageMin" : 3, // 케어 가능한 최소연령 기본값 0
    "coverageMax" : 5, // 케어 가능한 최대연령 기본값 200
    "intro" : "자기 소개"
  }
}
```

### [PATCH] /api/member/{userIdx}
- 회원 정보 수정
- 인증 Token 필요
- Request
```text
Header
Authrization : Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxd2VycnIiLCJhdXRoIjoic2l0dGVyLHVzZXIiLCJpYXQiOjE2ODc0MDEwMzMsImV4cCI6MTY4NzQwNzAzM30.DKpZj8KYS_SLC4Qu5to77KGQsdxCXSA0w2SGvsbjTmA
```
```json lines
{
  "name" : "이름",
  "birth" : "생년월일 yyyyMMdd 형식",
  "gender" : "m:남자/ f:여자",
  "email" : "email 주소",
  "role" : "Update 하고자하는 역할 명시", // user | sitter | user,sitter
  "user" : {
    "reqInfo" : "요청사항",
    "children" : [
      {
        "childIdx" : "update시 사용",
        "birth" : "생년월일 yyyyMMdd 형식",
        "gender" : "m:남자/ f:여자"
      }
    ]
  },
  "sitter" : {
    "coverageMin" : 3, // 케어 가능한 최소연령 기본값 0
    "coverageMax" : 5, // 케어 가능한 최대연령 기본값 200
    "intro" : "자기 소개"
  }
}
```

### [GET] /api/member/{userIdx}
- 회원 정보 조회
- Resource userIdx를 통해 DB조회
- 인증 Token 필요
- Request
```text
Header
Authrization : Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxd2VycnIiLCJhdXRoIjoic2l0dGVyLHVzZXIiLCJpYXQiOjE2ODc0MDEwMzMsImV4cCI6MTY4NzQwNzAzM30.DKpZj8KYS_SLC4Qu5to77KGQsdxCXSA0w2SGvsbjTmA
```
- 성공시 Response
```json lines
{
  "dateTime": "2023-06-22T21:00:00+09",
  "success": true,
  "response": {
    "userIdx": "12345",
    "userId": "id",
    "name": "이름",
    "birth": "20230622",
    "gender": "m or f",
    "email": "email@email.com",
    "role": "user|sitter|user,sitter",
    "user": { // role 값에 user가 포함되어 있으면 해당 Json포함
      "reqInfo": "요청사항",
      "children": [
        {
          "childIdx": "child index - 추후 update를 위한 정보",
          "birth": "20230622",
          "gender": "m or f"
        }
      ]
    },
    "sitter": { // role 값에 sitter가 포함되어 있으면 해당 Json포함
      "coverageMin": 3,
      "coverageMax": 5,
      "intro": "자기 소개"
    }
  },
  "error": null
}
```

### [PUT] /api/member/{userIdx}/child
- 자녀 정보 추가
- 인증 Token 필요
- Request
```text
Header
Authrization : Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxd2VycnIiLCJhdXRoIjoic2l0dGVyLHVzZXIiLCJpYXQiOjE2ODc0MDEwMzMsImV4cCI6MTY4NzQwNzAzM30.DKpZj8KYS_SLC4Qu5to77KGQsdxCXSA0w2SGvsbjTmA
```
```json lines
{
  "children": [
    {
      "birth": "20230622",
      "gender": "m or f"
    }
  ]
}
```

### [DELETE] /api/member/{userIdx}/child/{childIdx}
- 자녀 정보 삭제
- 인증 Token 필요
- Resource userIdx와 chlidIdx를 통해 삭제


### Todo
- JWT 복호화 Key Rolling
- Refresh Token


