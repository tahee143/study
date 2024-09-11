# 스프링 mvc 기본 기능

## 로깅 간단히 알아보기
#### 로깅 라이브러리
- 스프링 부트 라이브러리를 사용하면 스프링부트 로깅 라이브러리가 함께 포함
- SLF4J
  - 로그 라이브러리는 수 많은 라이브러리가 있는데 통합해 인터페이스로 제공하는 것 
- Logback
  - SLF4J 인터페이스 구현체, 실무에서 대부분 사용 
#### 로그 선언
```java
private Logger log = LoggerFactory.getLogger(getClass());
private static final Logger log = LoggerFactory.getLogger(Xxx.class);
@Slf4j : 롬복 사용
```
#### 로그호출
```java
log.info("hello");
```

#### 로그 레벨
- TRACE > DEBUG > INFO > WARN > ERROR
- 개발 서버는 debug 출력
- 운영 서버는 Info 출력

#### 올바른 로그 사용법
- `log.debug("data=" + data)`
  - 로그 출력 레벨이 info여도 해당 코드의 + 연산 실행되어 메모리, CPU 사용
- `log.debug("data=", data)`
  - 로그 출력 레벨이 info로 설정하면 아무일도 실행되지 않음 의미없는 연산 발생하지 않음

#### 로그 사용 장점
- 쓰레드 정보, 클래스 이름 같은 부가 정보 확인 가능, 출력 모양 조정 가능
- 로그 레벨에 따라 상황에 맞게 조절 가능
- 콘솔뿐만 아니라 파일이나 네트워크 등 로그를 별도의 위치에 남길 수 있음
- 파일로 남길 때 일별, 특정 용량에 따라 로그 분할 가능
- 성능도 System.out 보다 좋음

---

## 요청 매핑

#### 매핑 정보
- `@Controller`
  - 반환 값이 String이면 뷰 이름으로 인식, 뷰를 찾고 뷰가 랜더링
- `@RestController`
  - 반환 값 String을 HTTP 메시지 바디에 바로 입력

#### @RequestMapping
- 지정 URL 호출이 오면 메서드 실행
- 속성을 배열로 제공해 다중 URL 설정 가능 `{"/hello-basic", "/hello-go"}`
- ~~`/hello-basic`, `/hello-basic/` 두가지 요청은 다른 URL이지만 스프링은 같은 요청으로 처리~~ 
  - 스프링 3.0부터 다른 요청으로 처리
- `method` HTTP 메서드를 지정하지 않으면 HTTP 메서드와 무관하게 모두 호출
  - 지정된 메서드 제외 다른 메서드로 요청하면 405 반환

#### HTTP 메서드
- GET, HEAD, POST, PUT, PATCH, DELETE
- HTTP 메서드 매핑 축약
  - `@GetMapping`
  - `@PostMapping`
  - `@PutMapping`
  - `@DeleteMapping`
  - `@PatchMapping`

#### PathVariable(경로 변수) 사용
- 최근 HTTP API는 리소스 경로에 식별자를 넣는 스타일 선호 
  - `/mapping/user1`, `/user/2`
- URL 경로를 템플릿화 할 때 `@PathVariable`사용

#### 특정 파라미터 조건 매핑
- 특정 파라미터가 조건에 부합하면 호출
- mode=debug 조건에 맞아야지만 호출된다

#### 특정 헤더 요청 조건 매핑
- 특정 헤더 조건에 부합하면 호출

#### 미디어 타입 조건 매핑 - HTTP 요청 Content-Type, consume
- HTTP 요청의 Content-Type 헤더를 기반으로 미디어 타입으로 매핑
- 미디어 타입 조건에 맞지 않으면 415 상태코드(Unsupported Media Type)을 반환
- 컨트롤러 입장에선 정보를 소비하는 것이기 때문에 consume
- 
#### 미디어 타입 조건 매핑 - HTTP 요청 Accept, produce
- HTTP 요청의 Accept 헤더를 기반으로 미디어 타입으로 매핑
- 미디어 타입 조건에 맞지 않으면 406 상태코드(Not Acceptable)을 반환
- 컨트롤러 입장에선 생산해내야 하는 것이디 때문에 produce

---

## HTTP 요청

### 기본, 헤더 조회
- `HttpMethod` : HTTP 메서드 조회
- `Locale` : Locale 정보 조회
- `@RequestHeader MultiValueMap<String, String> headerMap` : 모든 HTTP 헤더를 MultiValueMap 형식으로 조회
  - `MultiValueMap` : Map과 유사한데 하나의 키에 여러 값을 받을 수 있음, 배열로 반환
- `@RequestHeader("host")` : 특정 HTTP 헤더 조회
- `@CookieValue(value = "myCookie", required = false)` : 특정 쿠키 조회

### 요청 파라미터
- `서블릿`에서 학습했던 HTTP 요청 메시지를 통해 클라이언트 서버로 데이터 전달하는 방법 3가지
  - GET - 쿼리 파라미터
    - `/url?userId=userA`
    - 메시지 바디 없이 URL의 쿼리파라미터에 데이터 포함해 전달
    - 검색, 필터, 페이징 등에서 자주 사용
  - POST - HTML Form
    - `content-type: application/x-www-form-urlencoded`
    - 메시지 바디에 쿼리 파라미터 형싱으로 전달
    - 회원가입, 상품 주문, HTML Form 사용
  - HTTP message body
    - HTTP API에서 주로 사용, JSON, XML, TEXT
    - 데이터 형식은 주로 JSON 사용
    - POST, PUT, PATCH
  
#### 쿼리 파라미터, HTML
- GET 쿼리 파라미터나 POST HTML Form 전송 방식이든 둘다 형식이 같아 구분없이 조회 가능
- 이것을 간단하게 `요청 파라미터 조회`라 함
- `HttpServletRequest`의 `request.getParameter()` 사용하면 두가지 요청 파라미터 조회 가능

#### @RequestParam
- 스프링이 제공하는 `@RequsetParam`을 사용하면 요청 파라미터를 매우 편리하게 사용가능
- `@RequsetParam`의 `name(value)` 속성이 파라미터 이름으로 바인딩
- `@ResponseBody`를 사용하면 view 조회대신 HTTP message body에 직접 내용 입력 가능
- HTTP 파라미터 이름이 변수 이름과 같으면 `@RequsetParam(name="xx")` 생략 가능
- String, int 등의 단순 타입이면 `@RequsetParam`도 생략 가능
- 파라미터 필수 여부 - requestParamRequired
  - `@RequestParam(required = true)` 파라미터 필수 여부 지정
  - 필수 파라미터 없음 -> 400 예외 발생
  - 파라미터 이름만 있고 값이 없는 경우 -> 빈 문자열로 통과
  - 기본형(primitive) null 요청 -> 500 예외 발생, `Integer`로 변경하거나 `defaultValue` 사용
- 기본 값 적용 - requestParamDefault
  - 파라미터에 값이 없는 경우 `defaultValue`를 사용해 기본값 설정 가능
  - `defaultValue`를 사용하면 `required`는 의미가 없어짐(기본값이 존재하기 때문에 값이 무조건 들어감)
  - 빈 문자열인 경우에도 기본값 적용
- 파라미터를 Map으로 조회하기 - requestParamMap
  - 파라미터의 값이 1개가 확실하다면 `Map` 사용, 여러 개라면 `MultiValueMap` 사용

#### @ModelAttribute
- @ModelAttribute 적용
  - 요청 파라미터를 받아서 필요한 객체를 만들고 그 객체에 값을 넣어주는 과정을 스프링이 자동화해줌
  - `@ModelAttribute`가 있으면 객체를 생성하고 요청 파라미터의 이름으로 객체의 프로퍼티를 찾음
  - 해당 프로퍼티의 setter를 호출해서 파라미터 값을 바인딩함
  - 바인딩 오류(`BindException`) int가 필요한 곳에 문자열이 들어가면 발생 -> 검증부분에서
- @ModelAttribute 생략 - modelAttributeV2
  - `@ModelAttribute`는 생략가능
  - `@RequestParam`도 생략할 수 있으니 혼란이 발생할 수 있음
  - 생략시 규칙
    - `String`, `int`, `Integer` 같은 단순 타입 = `@RequestParam`
    - 나머지 = `@ModelAttribute` (argument resolver로 지정해둔 타입은 지정되지 않음)

### 요청 메시지

#### 단순 텍스트
- `서블릿`에서 학습한 `HTTP message body`에 데이터를 직접 담아 요청하는 방법
  - HTTP API에서 주로 사용 JSON, XML, TEXT
  - 데이터 형식은 JSON
  - POST, PUT, PATCH
- 메시지 바디를 통해 직접 데이터가 넘어오는 경우 `@RequestParam`, `@ModelAttribute` 사용 불가
  - InputStream(Reader): HTTP 요청 메시지 바디의 내용을 직접 조회 
  - OutputStream(Writer): HTTP 응답 메시지의 바디에 직접 결과 출력
- HttpEntity
  - `HttpMessageConverter`를 사용해 HTTP header, body를 편리하게 조회
  - 응답에서도 메시지 바디 정보 직접 반환 가능
  - `HttpEntity`를 상속받은 `RequestEntity`, `ResponseEntity` 객체들도 같은 기능 제공
- @RequestBody
  - `@RequestBody`를 사용하면 HTTP 메시지 바디 정보 편리하게 조회 가능
    - 헤더 정보가 필요하다면 `HttpEntity`를 사용하거나 `@RequestHeader`사용
- 요청 파라미터 vs HTTP 메시지 바디
  - 요청 파라미터를 조회하는 기능: `@RequestParam` , `@ModelAttribute` 
  - HTTP 메시지 바디를 직접 조회하는 기능: `@RequestBody`

#### JSON
- HTTP API에서 주로 사용하는 JSON 데이터 형식 조회
- HttpServletRequest 직접 문자 변환
  - `HttpServletRequest`를 사용해서 직접 HTTP 메시지 바디에서 데이터를 읽고 문자로 변환
- @RequestBody 문자 변환
  - `@RequestBody`를 사용해 HTTP 메시지에서 데이터를 꺼내고 메시지 바디를 저장
  - 문자로 된 JSON 데이터 메시지 바디를 `objectMapper`를 통해 자바 객체로 변환해 사용
- @RequestBody, HttpEntity 객체 변환
  - `HttpEntity`, `@RequestBody`를 사용하면 HTTP 메시지 컨버터가 HTTP 메시지 바디의 내용을 원하는 문자나 객체등으로 변환
  - `@RequestBody`를 생략하면 `@ModelAttribute`가 적용되므로 생략 불가능
  - `@ResponseBody`
    - 응답의 경우에도 `HttpEntity`, `@ResponseBody` 를 사용하면 해당 객체를 HTTP 메시지 바디에 직접 넣어줄 수 있음
- `@RequestBody` 요청
  - JSON 요청 -> HTTP 메시지 컨버터 -> 객체
- `@ResponseBody` 응답
  - 객체 -> HTTP 메시지 컨버터 -> JSON 응답

---

## HTTP 응답

### 정적 리소스, 뷰 템플릿
- 스프링(서버)에서 응답 데이터를 만드는 방법 3가지
  - 정적 리소스
    - 웹 브라우저에 정적인 HTML, css, js를 제공할 때는 정적 리소스 사용
  - 뷰 템플릿
    - 웹 브라우저에 동적인 HTML을 제공할 때는 뷰 템플릿 사용
  - HTTP 메시지
    - HTTP API를 제공하는 경우에는 데이터를 전달해야함, HTTP 메시지 바디에 JSON 같은 형식으로 데이터를 실어 보냄

#### 정적 리소스
- 스프링 부트는 클래스패스의 `/static`, `/public`, `/resources`, `/META-INF/resources` 디렉토리에 있는 정적 리소스를 제공
- `src/main/resources`는 리소스 보관, 클래스패스의 시작 경로임, 이 경로에 리소스를 넣어두면 스프링 부트가 정적 리소스로 서비스 제공
- 정적 리소스는 해당 파일을 변경 없이 그대로 서비스함

#### 뷰 템플릿
- 뷰 템플릿을 거쳐 HTML이 생성되고 뷰가 응답을 만들어 전달
- 일반적으로 HTML을 동적으로 생성하는 용도로 사용
- String 반환하는 경우 - View or HTTP 메시지(responseViewV2)
  - `@ResponseBody`가 없으면 `resources/hello`로 뷰 논리 이름이 반환되고 뷰 리졸버 실행되어 뷰를 찾아 렌더링
  - `@ResponseBody`가 있으면 뷰 리졸버를 실행하지 않고 HTTP 메시지 바디에 직접 `resources/hello` 문자가 입력
- Void 반환하는 경우(responseViewV3)
  - `@Controller`를 사용하고 `HttpServletResponse`, `OutputStream(Writer)` 같은 HTTP 메시지 바디를 처리하는 파라미터가 없으면 요청 URL을 참고해 논리 뷰 이름으로 사용
  - 이 방식은 명시성이 떨어지고 딱 맞는 경우도 많이도 없어서 권장하지 않음

### HTTP 응답 - HTTP API, 메시지 바디에 직접 입력 
- HTTP API를 제공하는 경우 HTML이 아니라 데이터를 전달해야함
- HTTP 메시지 바디에 JSON 같은 형식으로 데이터를 실어 보냄
- 참고로 HTML이나 뷰 템플릿을 사용해도 HTTP 응답 메시지 바디에 HTML 데이터가 담겨서 전달됨
- responseBodyV1
  - `HttpServletResponse` 객체를 통해서 HTTP 메시지 바디에 직접 응답 메시지 전달
- responseBodyV2
  - `ResponseEntity` HTTP 메시지 컨버터를 통해서 HTTP 메시지를 직접 입력 가능, HTTP 응답 코드를 설정해 전달
- responseBodyV3
  - `@ResponseBody`를 사용해 view를 사용하지 않고 HTTP 메시지 컨버터를 통해서 HTTP 메시지를 직접 입력 가능
- responseBodyJsonV1
  - `ResponseEntity`를 사용해 HTTP 메시지 컨버터를 통해서 JSON 형식으로 변환해 반환
- responseBodyJsonV2
  - `ResponseEntity`는 HTTP 응답 코드를 설정할 수 있는데 `@ResponseBody`같이 사용하면 응답 코드 설정이 어려움
  - `@ResponseStatus(HttpStatus.OK)` 애노테이션을 사용해 지정 가능
- `@RestController`
  - `@Controller` 대신에 사용하면 컨트롤러 모두 `@ResponseBody`가 적용
  - 따라서 뷰 템플릿을 사용하는 것이 아니라 HTTP 메시지 바디에 직접 데이터 입력

---

## HTTP 메시지 컨버터
- 뷰 템플릿으로 HTML을 생성해서 응답하는 것이 아니라 HTTP API처럼 JSON 데이터를 HTTP 메시지 바디에서 직접 읽거나 쓰는 경우 HTTP 메시지 컨버터를 사용하면 편리함
- `@ResponseBody`를 사용
  - HTTP BODY에 문자 내용 직접 반환
  - `viewResolver` 대신에 `HttpMessageConverter`가 동작
  - 기본 문자, 객체, byte 처리 등 기타 여러 HttpMessageConverter가 기본으로 등록 되어있음
  - 응답의 경우 클라이언트의 HTTP Accept 헤더와 서버의 컨트롤러 반환 타입 정보를 조합해 HttpMessageConverter 선택

#### HTTP 메시지 컨버터 적용
- 스프링 MVC는 다음 경우에 HTTP 메시지 컨버터를 적용함
- HTTP 메시지 컨버터는 HTTP 요청, 응답 둘 다 사용됨
- HTTP 요청 : `@RequestBody`, `HttpEntity(RequesEntity)`
- HTTP 응답 : `@ResponseBody`, `HttpEntity(ResponseEntity)`

### 스프링 부트 기본 메시지 컨버터
- 대상 클래스 타입과 미디어 타입 둘을 체크해 사용여부 결정
- 해당 컨버터에 만족하지 않으면 다음 컨버터로 우선순위가 넘어가며 결정

#### 3가지 주요 메세지 컨버터
1. `ByteArrayHttpMessageConverter` : `byte[]` 데이터를 처리
  - 클래스 타입 : `byte[]`
  - 미디어 타입 : `*/*`
  - 요청 예) `@RequestBody byte[] data`
  - 응답 예) `@ResponseBody return byte[]`
  - 쓰기 미디어 타입 : `application/octet-stream`
2. `StringHttpMessageConverter` : String 문자로 데이터 처리
  - 클래스 타입 : `String`
  - 미디어 타입 : `*/*`
  - 요청 예) `@RequestBody String data`
  - 응답 예) `@ResponseBody return "ok"` 
  - 쓰기 미디어 타입 : `text/plain`
3. `MappingJackson2HttpMessageConverter` : application/json
  - 클래스 타입 : 객체 또는 `HashMap`
  - 미디어 타입 : `application/json` 관련
  - 요청 예) `@RequestBody HelloData data`
  - 응답 예) `@ResponseBody return data` 
  - 쓰기 미디어 타입 : `application/json` 관련

#### HTTP 요청 데이터 읽기
- HTTP 요청이 오고, 컨트롤러에서 `@RequestBody` , `HttpEntity` 파라미터를 사용
- 메시지 컨버터가 메시지를 읽을 수 있는지 확인하기 위해 `canRead()` 를 호출
  - 대상 클래스 타입을 지원?
    - 예) `@RequestBody` 의 대상 클래스 ( `byte[]` , `String` , `HelloData` )
  - HTTP 요청의 Content-Type 미디어 타입을 지원?
    - 예) `text/plain` , `application/json` , `*/*`
- `canRead()` 조건을 만족하면 `read()` 를 호출해서 객체 생성하고, 반환

#### HTTP 응답 데이터 생성
- 컨트롤러에서 `@ResponseBody` , `HttpEntity` 로 값이 반환
- 메시지 컨버터가 메시지를 쓸 수 있는지 확인하기 위해 `canWrite()` 를 호출
  - 대상 클래스 타입을 지원?
    - 예) return의 대상 클래스 ( `byte[]` , `String` , `HelloData` )
  - HTTP 요청의 Accept 미디어 타입을 지원?(더 정확히는 `@RequestMapping` 의 `produces` ) 
    - 예) `text/plain` , `application/json` , `*/*`
- `canWrite()` 조건을 만족하면 `write()` 를 호출해서 HTTP 응답 메시지 바디에 데이터를 생성

---

## 요청 매핑 핸들러 어뎁터 구조
- HTTP 메시지 컨버터는 스프링 MVC 어디쯤에서 사용되는 걸까?
  - `@RequestMapping`을 처리하는 핸들러 어댑터인 `RequestMappingHandlerAdapter`가 중요

### RequestMappingHandlerAdapter 동작 방식
#### ArgumentResolver
- `HandlerMethodArgumentResolver`인데 줄여서 `ArgumentResolver`라고 부름
- 애노테이션 기반의 컨트롤러는 매우 다양한 파라미터 사용가능
  - `HttpServletRequest`, `Model`, `@RequestParam`, `@ModelAttribute`, `@RequestBody`, `HttpEntity`까지 매우 큰 유연함
- 파라미터를 우연하게 처리할 수 있는 이유가 `ArgumentResolver` 덕분
- 애노테이션 기반 컨트롤러를 처리하는 `RequestMappingHandlerAdapter`가 `ArgumentResolver`를 호출해 컨트롤러가 필요로하는 다양한 파라미터 값(객체)를 생성
- 파라미터 값이 모두 준비되면 컨트롤러를 호출하면서 값을 넘겨줌
- 동작 방식
  - `supportParameter()`를 호출해서 해당 파라미터를 지원하는지 확인
  - 지원하면 `resolveArgument()`호출해서 실제 객체 생성
  - 생성된 객체가 컨트롤러 호출시 넘어감

#### ReturnValueHandler
- `HandlerMethodReturnValueHandler`를 줄여서 `ReturnValueHandler`라 부름
- 응답 값을 변환하고 처리함
  - `ModelAndView`, `@ResponseBody`, `HttpEntity`, `String` 등
- 컨트롤러에서 String으로 뷰 이름을 반환할때 동작하는 이유가 `ReturnValueHandler`때문

### HTTP 메시지 컨버터 위치
#### HTTP 메시지 컨버터 위치
- 요청
  - `@RequestBody`와 `HttpEntity`를 각각 처리하는 `ArgumentResolver`가 있음
  - `ArgumentResolver`들이 HTTP 메시지 컨버터를 사용해서 필요한 객체를 생성
- 응답
  - `@ResponseBody`와 `HttpEntity` 를 각각 처리하는 `ReturnValueHandler`가 있음
  - `ReturnValueHandler`에서 HTTP 메시지 컨버터를 호출해서 응답 결과를 생성
- 스프링 MVC는 `@RequestBody` `@ResponseBody`가 있으면
  - `RequestResponseBodyMethodProcessor(ArgumentResolver, ReturnValueHandler 둘다 구현)` 사용
- `HttpEntity` 가 있으면
- `HttpEntityMethodProcessor(ArgumentResolver, ReturnValueHandler 둘다 구현)` 사용