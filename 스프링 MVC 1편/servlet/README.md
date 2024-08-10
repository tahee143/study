## HttpServletRequest 개요
#### HttpServletRequest 역할
- 서블릿은 HTTP 요청 메시지를 파싱하고 그 결과를 `HttpServletRequest` 객체에 담아서 제공
- 
#### HTTP 요청 메시지
- START LINE
  - HTTP 메소드
  - URL
  - 쿼리 스트링
  - 스키마, 프로토콜
- 헤더
- 바디
  - form 파라미터 형식 조회
  - message body 데이터 직접 조회

#### HttpServletRequest 부가 기능
- 임시 저장소 기능
  - 해당 HTTP 요청이 시작부터 끝까지 유지되는 임시 저장소 기능
  - 저장 : `request.setAttribute(name, value)`
  - 조회 : `request.getAttribute(name`
- 세션 관리 기능
  - `request.getSession(create: true)`

--- 

## HTTP 요청 데이터 개요

### 데이터 전달 방법
- HTTP 요청 메시지를 통해 클라이언트에서 서버로 데이터를 전달하는 방법

- **GET - 쿼리 파라미터**
  - `/url?username=lee&age=20`
  - 메시지 바디없이 URL의 쿼리 파라미터에 데이터 포함해 전달
  - 검색, 필터, 페이징에서 많이 사용

- **POST - HTML Form**
  - `content-type: application/x-www-form-urlencoded`
  - 메시지 바디에 쿼리 파라미터 형식으로 전달 `username=lee&age=20`
  - 회원가입, 상품 주문, HTML Form 사용

- **HTTP message body**
  - HTTP API에서 주로 사용, JSON, XML, TEXT
  - 데이터 형식은 주로 JSON 사용
  - POST, PUT, PATCH

### HTTP 요청 데이터 - GET 쿼리 파라미터
- 쿼리 파라미터는 URL에 `?`로 시작을 보낼 수 있고 추가 파라미터는 `&`로 구분
- 파라미터 전체 조회 : `request.getParameterNames()`
- 단일 파라미터 조회 : `request.getParameter()`
- 이름이 같은 복수 파라미터 조회 : `request.getParameterValues()`
- 복수 파라미터에서 단일 파라미터 조회
  - 이름이 중복일때 `getParameter()`를 사용하면 `getParameterValues()`의 첫번째 값을 반환

### HTTP 요청 데이터 - POST HTML Form
- POST의 HTML Form을 전송하면 웹 브라우저가 HTTP 메시지를 만듦
  - 요청 URL : http://localhost:8080/request-param
  - content-type: `application/x-www-form-urlencoded`
  - message body : `username=hello&age=20`
- `application/x-www-form-urlencoded` 형식은 GET 쿼리 파라미터 형식과 같아 `request.getParameter()`로 조회가능
- content-type은 HTTP 메시지 바디의 데이터 형식 지정
  - **GET 쿼리 파라미터**는 메시지 바디 사용 ❌ content-type ❌
  - **POST HTML Form**은 메시지 바디에 데이터를 포함⭕️ content-type ⭕️

### HTTP 요청 데이터 - API 메시지바디
#### 단순 텍스트 전송
- `request.getInputStream()` : 메시지바디에 있는 내용을 byte 코드로 반환
- `StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8)` : 바이트 코드를 String으로 변환, 인코딩 지정필요

#### JSON
- JSON 형식 전송
  - 요청 URL : http://localhost:8080/request-body-json
  - content-type: `application/json`
  - message body : `{"username": "hello", "age": 20}`
  - 결과 : `messageBody = {"username": "hello", "age": 20}`
- 자바 객체로 json 결과를 파싱하기 위해 `jackson(ObjectMapper)` 라이브러리 사용

---

## HttpServletResponse 기본 사용법
- HTTP 응답 메시지 생성
  - HTTP 응답 코드 지정
  - 헤더 생성
  - 바디 생성
- 편의기능 제공
  - Content-Type, Redirect, 쿠키
- 