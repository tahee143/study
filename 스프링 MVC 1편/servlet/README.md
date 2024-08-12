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

### 적용
- Model은 HttpServletRequest 객체를 사용, request는 내부에 데이터 저장소를 가지고 있음
- `request.setAttribute()`, `request.getAttribute()` 를 사용하면 데이터 보관, 조회 가능
- `dispatcher.forward()` : 다른 서블릿이나 JSP로 이동, 서버 내부에서 다시 호출 발생
- `/WEB-INF` : JSP가 있으면 외부에서 직접 JSP 호출 불가, 컨트롤러를 통해서만 호출

#### redirect vs forward
- redirect : 실제 클라이언트(웹 브라우저)에 응답이 나갔다가 클라이언트가 리다이렉트 경로로 다시 요청, 클라이언트가 인지할 수 있고 URL 경로도 실제 변경
- forward : 서버 내부에서 일어나는 호출, 클라이언트가 전혀 인지 못 함

### 한계
- MVC 덕분에 컨트롤러 로직과 뷰 로직을 확실하게 분리했지만 단점이 있음
- forward 중복
  - view로 이동하는 코드가 항상 중복 호출
- viewPath 중복
  - prefix `/WEB-INF/views`
  - suffix `.jsp`
  - 다른 뷰 템플릿을 사용한다면 전체 코드 변경이 필요함
- 사용하지 않는 코드
  - `HttpServletRequest`, `HttpServletResponse` 사용하지 않을때도 적어야 함, 테스트 케이스 작성도 어려움
- 공통 처리가 어려움
  - 기능이 복잡해지면 공통으로 처리할 기능이 많아짐
  - 메서드로 뽑아도 매번 메서드 호출, 누락, 중복 문제가 있음

---

## MVC 프레임워크 만들기
### 프론트 컨트롤러 패턴 소개
- MVC 패턴을 도입했을 때 중복과 공통처리에 한계가 있음
- 컨트롤러를 호출 전에 공통 기능을 먼저 처리하는 **프론트 컨트롤러 패턴**을 도입

### 프론트 컨트롤러 특징
- 프론트 컨트롤러 서블릿 하나로 클라이언트 요청을 받음
- 프론트 컨트롤러가 요청에 맞는 컨트롤러를 찾아서 호출
- 공통 처리를 프론트 컨트롤러가 해결
- 나머지 컨트롤러는 서블릿을 사용하지 않아도 됨

### 프론트 컨트롤러 V1
#### ControllerV1
  - 서블릿과 비슷한 모양의 컨트롤러 인터페이스
  - 각 컨트롤러는 인터페이스를 구현

#### 프론트 컨트롤러
- urlPatterns
  - `urlPatterns = "/front-controller/v1/*"` : 포함한 하위 모든 요청을 받음
- controllerMap
  - key : 매핑 URL
  - value : 호출될 컨트롤러
- service()
  - `requestURI` 조회해 실제 호출할 컨트롤러를 `controllerMap` 에서 찾음
  - 해당하는 컨트롤러가 없으면 404(SC_NOT_FOUND) 상태 코드를 반환
  - 컨트롤러 찾고 `controller.process(request, response)` 호출해 해당 컨트롤러를 실행
- JSP
  - 이전 MVC것 그대로 사용

### 프론트 컨트롤러 V2 - 뷰 분리
모든 컨트롤러에서 직접 뷰를 forward하고 뷰로 이동하는 부분에 중복

```java
  String viewPath = "/WEB-INF/views/new-form.jsp";
  RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
  dispatcher.forward(request, response);
```
#### MyView
  - 뷰를 처리하는 객체
  - 컨트롤러가 MyView를 반환하면 뷰 객체에서 `forward` 로직을 수행해서 JSP 실행

#### ControllerV2
  - 반환 타입 `MyView` 
  - 프론트 컨트롤러는 각 컨트롤러 호출 결과로 `MyView`를 반환받음, `MyView.render()`호출하면 JSP 실행 
  - 각 컨트롤러는 `dispatcher.forward()`를 생성하고 호출하지 않아도 됨
  - `MyView` 객체를 생성만해서 반환하면 뷰 객체가 `forward` 로직 수행

### 프론트 컨트롤러 V3 - 모델 추가
#### 서블릿 종속성 제거
- 컨트롤러 입장에서 `HttpServletRequest`, `HttpServletResponse`는 꼭 필요하지 않음
- 컨트롤러가 서블릿 기술을 사용하지 않아도 됨
  - 요청 파라미터는 Map 이용
  - request 객체를 모델로 사용하지 않고 별도의 Model 객체를 생성해 반환 

#### 뷰 이름 중복 제거
- 각 컨트롤러에서 뷰 이름에 중복이 있음
- 중복을 제거하고 뷰의 위치가 변경되도 코드 변경이 없도록 가능
  - 컨트롤러는 뷰의 논리 이름을 반환
  - 실제 물리 위치는 프론트 컨트롤러에서 처리

#### ModelView
- 서블릿 종속성을 제거하기 위해 Model을 직접 생성하고 view 이름도 전달하는 객체

#### ControllerV3
- 서블릿 종속성을 제거해 컨트롤러에서 `HttpServletRequest` 사용 불가
- 파라미터는 프론트 컨트롤러 paramMap에 담아 호출
- 뷰 이름과 Model 데이터를 포함하는 ModelView 객체를 반환

#### viewResolver
- 컨트롤러가 반환한 뷰의 논리 이름을 실제 물리 이름으로 변경
- 실제 물리 경로가 있는 MyView 객체 반환

#### MyView
- 뷰 객체를 통해 HTML 화면 렌더링
- ModelView에 있던 데이터를 `request.setAttribute()`에 담아둠
- `forward` 로직을 수행해서 JSP 실행