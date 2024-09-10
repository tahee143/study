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

#### 기본, 헤더 조회
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
