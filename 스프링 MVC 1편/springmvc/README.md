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
