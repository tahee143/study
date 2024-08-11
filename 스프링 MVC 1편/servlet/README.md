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

## HttpServletResponse 
### HTTP 응답 데이터 - 기본 사용법
- HTTP 응답 메시지 생성
  - HTTP 응답 코드 지정
  - 헤더 생성
  - 바디 생성
- 편의기능 제공
  - Content-Type, Redirect, 쿠키

### HTTP 응답 데이터 - 단순 텍스트, HTML
- 단순 텍스트 응답
  - `writer.println("ok")`
- HTML 응답
  - Content-Type을 `text/html`로 지정

### HTTP 응답 데이터 - API JSON
- Content-Type을 `application/json`로 지정
- `objectMapper.writeValueAsString()`를 사용해 객체를 JSON 문자로 변경할 수 있음
- `application/json`은 스펙상 utf-8 형식을 사용하도록 정의

---

## 서블릿과 JSP의 한계
#### 서블릿과 JSP
- 서블릿으로 개발할 때 뷰 화면을 위한 HTML에 자바 코드가 섞여 지저분하고 복잡
- JSP를 사용한 덕분에 HTML 작업이 깔끔하게 가져가고 중간 동적으로 변경이 필요한 부분 자바 코드 적용
- JSP 코드를 보면 데이터 조회 리포지토리 등 코드가 모두 노출
- JSP에 너무 많은 역할

#### MVC 패턴 등장
- 비즈니스 로직은 서블릿처럼 다른 곳에서 처리
- JSP는 목적에 맞게 HTML로 화면 구성에 집중

---

## MVC 패턴
### 개요
- 하나의 서블릿과 JSP만으로 비즈니스 로직과 뷰 렌더링까지 모두 처리하면 너무 많은 역할
- 비즈니스 로직과 뷰의 변경 라이프 사이클이 달라 유지보수가 좋지 않음
- JSP 같은 뷰 템플릿은 화면 렌더링하는데 최적화 되어있어 뷰 렌더링 업무만 담당하는 것이 좋음
- JSP로 처리하던 것을 컨트롤러와 뷰라는 영역으로 서로 역할을 나눈 것이 MVC패턴
- Controller
  - HTTP 요청을 받아서 파라미터 검증, 비즈니스 로직 실행
  - 뷰에 전달할 결과 데이터를 조회에 모델에 담음
- Service
  - 컨트롤러에 비즈니스 로직을 두면 컨트롤러에 너무 많은 역할
  - 비즈니스 로직을 서비스 계층에서 처리
  - 컨트롤러는 비즈니스 로직이 있는 서비스를 호출하는 역할담당
- Model
  - 뷰에 출력할 데이터 담음
  - 뷰가 필요한 데이터를 모델에 담아 전달하기 때문에 뷰는 비즈니스 로직이나 데이터 접근을 알 필요 없음
- View
  - 모델에 담겨있는 데이터 사용해 화면 렌더링에 집중
  - HTML 생성 부분
