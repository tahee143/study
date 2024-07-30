## 비즈니스 요구사항과 설계
- 회원
	- 회원은 가입하고 조회할 수 있음
	- 회원은 일반과 VIP 두 등급이 있음
	- 회원 데이터는 자체 DB를 구축할 수 있고, 외부 시스템과 연동할 수 있음(미확정)
- 주문과 할인 정책
	- 회원은 상품을 주문할 수 있음
	- 회원 등급에 따라 할인 정책을 적용할 수 있음
	- 할인 정책은 모든 VIP는 1000원 할인해주는 고정 금액 할인 적용(미확정)
	- 할인 정책은 변경 가능성이 높음

**확정되지 않은 요구사항을 위해 객체 지향 설계 방법을 적용하여 인터페이스를 만들고 구현체를 갈아끼울 수 있도록 설계**

---

### 회원 도메인 설계의 문제점
- 코드 설계상 문제점? 
- 다른 저장소로 변경 시 OCP 원칙 준수?
- DIP 지키고 있는지?
- **의존관계가 인터페이스 뿐만 아니라 구현까지 모두 의존하는 문제점이 있다**

  -> MemberServiceImpl을 보면 인터페이스와 구현체 모두 의존

---

## 주문과 할인 도메인 설계
1. 주문 생성 : 클라이언트는 주문 서비스에 주문 생성을 요청
2. 회원 조회 : 할인을 위해서는 회원 등급 필요, 주문 서비스는 회원 저장소에서 회원 조회
3. 할인 적용 : 주문 서비스는 회원 등급에 따른 할인 여부를 할인 정책에 위임
4. 주문 결과 반환 : 주문 서비스는 할인 결과를 포함한 주문 결과 반환(예제용으로 단순화하여 주문 결과만 반환)

역할과 구현을 분리

---

## 새로운 할인 정책 개발
- 기존은 할인 금액을 1000원으로 고정했지만 새로운 할인 정책은 주문 금액당 할인하는 정률% 할인

### 새로운 할인 정책 적용과 문제점
할인 정책을 변경하기 위해선 클라이언트인 OrderServiceImpl 수정 필요
- 역할과 구현을 분리 ⭕️
- 다형성도 활용하고 인터페이스와 구현 객체를 분리 ⭕️
- OCP, DIP 객체지향 설계 원칙을 준수? ❌
  - `prvate final DiscountPolicy discountPolicy = new FixDiscountPolicy();` ➡️ `private final DiscountPolicy discountPolicy = new RateDiscountPolicy();`
  - DIP : 주문 서비스 클라이언트는 DiscountPolicy 인터페이스에 의존하면서 DIP 준수한 듯 하지만 추상(인터페이스)뿐만 아니라 구체(구현) 클래스에도 의존하고 있음
  - OCP : 기능을 확장해서 변경하면 클라이언트 코드 수정이 필요, 고정할인에서 정률할인으로 변경할 때 코드 변경 필요

### 해결방안?
- 구현체 의존을 제거하고 추상에만 의존하도록 코드 수정 ➡️ `private DiscountPolicy discountPolicy;`
- 실행시 NPE 발생
- 💡 클라이언트에 **구현객체를 대신 생성하고 주입해주는 과정이 필요**

---

## 관심사의 분리
### AppConfig
- 애플리케이션의 전체 동작 방식을 구성하기 위해 **구현 객체를 생성**하고 **연결**하는 책임을 가지는 별도의 설정 클래스
- AppConfig에서 실제 동작에 필요한 구현 객체를 생성, 생성한 객체 인스턴스의 참조를 생성자를 통해 주입(연결)
  ``` java
	 public MemberService memberService(){
       return new MemberServiceImpl(new MemoryMemberRepository());
	 } // 생성자 주입
  ```
  - `MemberServiceImpl` ➡️ `MemoryMemberRepository`
  - `OrderServiceImpl` ➡️ `MemoryMemberRepository`, `FixDiscountPolicy`

### DIP 완성, 관심사 분리
- DIP
  - 클라이언트는 더이상 구체 클래스에 의존하지 않음
  - 클라이언트는 **추상만 의존**하고 구체 클래스를 몰라도 됨
- 관심사 분리
  - 구현 객체는 외부(`AppConfig`)에 의해서 결정
  - 의존관계에 대한 고민은 모두 외부에서 처리되고 실행에만 집중
  - **객체를 생성하고 연결하는 역할**과 **실행하는 역할**이 명확하게 분리

### 의존관계 주입 Dependency Injection, DI
- 클라이언트 입장에서 의존관계를 외부에서 주입

--- 

## AppConfig 리팩터링
```java
public MemberService memberService(){
    return new MemberServiceImpl(memberRepository());
}

private MemberRepository memberRepository() {
    return new MemoryMemberRepository();
} // memberRepository 구현을 따로 메서드로 빼줌
```
- 기존 코드 ➡️ memberService, orderService에 `new MemoryMemberRepository()` 중복이 있고, 역할에 따른 구현이 잘 안보임 
- 리팩토링 ➡️ 역할과 구현 클래스를 분리해 애플리케이션 전체 구성이 어떻게 되어있는지 빠르게 파악 가능

---

## 새로운 구조와 할인 정책 적용
- `AppConfig` 등장 ➡️`사용영역`과 `구성영역`으로 분리됨
- OCP, DIP 객체지향 설계 원칙을 준수? ⭕
  - DIP : 주문 서비스 클라이언트는 DiscountPolicy 인터페이스에만 의존, 구체(구현) 클래스는 주입받음
  - OCP : 기능을 확장, 변경시 클라이언트 코드 수정 불필요, AppConfig에서 모두 해결

---

## 객체 지향 설계 원칙 적용
#### SRP 단일 책임 원칙
- 클라이언트 객체는 직접 구현 객체를 생성, 연결, 실행하는 책임을 가지고 있었음
- AppConfig를 이용해 관심사 분리
  - 👉구현 객체를 생성하고 연결하는 책임을 AppConfig가 담당
  - 👉클라이언트는 객체를 실행하는 책임 담당

#### DIP 의존관계 역전 원칙
- 할인 정책을 변경하기 위해선 클라이언트 코드 수정이 필요
- 클라이언트는 추상화 인터페이스와 구체화 구현 클래스 모두 의존
  - 👉클라이언트는 추상화 인터페이스만 의존하도록 변경
  - 👉AppConfig가 객체 인스턴스를 생성해서 클라이언트 코드에 의존관계를 주입

#### OCP 개방폐쇄의 원칙
- 다형성을 사용하고 클라이언트가 DIP를 지킴
- 애플리케이션을 사용 영역과 구성 영역으로 나눔
- AppConfig가 의존 관계를 변경해도 클라이언트 코드를 변경하지 않아도 됨
  - 👉소프트웨어 요소를 새롭게 확장해도 사용 영역의 변경은 닫혀있다

---

## IoC, DI, 컨테이너
### 제어의 역전 IoC(Inversion of Control)
- 기존엔 클라이언트 구현 객체가 스스로 필요한 서버 구현 객체를 생성, 연결, 실행함    
👉구현 객체가 프로그램의 제어 흐름을 조종
- AppConfig 등장이후 구현 객체는 로직만 실행    
👉프로그램 제어 흐름 권한은 AppConfig가 가지고 있음
- 프로그램의 제어 흐름을 직접 제어하지 않고 외부에서 관리하는 것을 `제어의 역전 IoC`라고 함

    ##### 📌 프레임워크 vs 라이브러리
    - 프레임워크가 코드를 제어하고 대신 실행하면 `프레임워크`
    - 작성한 코드가 직접 제어의 흐름을 담당하면 `라이브러리`

### 의존관계 주입 DI(Dependency injection)
#### 📌정적인 클래스 의존관계
- import 코드로 의존관계를 쉽게 파악 가능
- 애플리케이션 실행없이 분석 가능

#### 📌동적인 객체 인스턴스 의존관계
- 실행 시점(런타임)에 외부에서 실제 구현 객체를 생성, 클라이언트에 구현한 객체 인스턴스 참조값 전달, 클라이언트와 서버의 실제 의존관계 연결되는 것 `의존관계 주입`

#### 📌의존관계 주입 장점
- 클라이언트 코드를 변경하지 않고 클라이언트가 호출하는 대상의 타입 인스턴스 변경 가능
- 정적인 클래스 의존관계를 변경하지 않고 동적인 객체 인스턴스 의존관계를 쉽게 변경할 수 있음

### IoC 컨테이너, DI 컨테이너
- AppConfig처럼 객체를 생성하고 관리하면서 의존관계를 연결해주는 것을 `IoC 컨테이너` 또는 `DI 컨테이너`
- 의존관계 주입에 초점을 맞춰 주로 `DI 컨테이너`라고 함

---

## 스프링으로 전환하기
### 스프링 컨테이너
- `ApplicationContext` == 스프링 컨테이너
- 기존엔 AppConfig를 사용해 직접 객체 생성하고 DI 했지만 이제는 스프링 컨테이너를 이용
- 스프링 컨테이너는 `@Configuration` 붙은 `AppConfig`를 구성정보로 사용
- `AppConfig`에 `@Bean` 붙은 메서드를 모두 호출해 반환된 객체를 스프링 컨테이너에 등록
- 스프링 컨테이너에 등록된 객체가 `스프링 빈`
- `@Bean` 붙은 메서드의 명을 스프링 빈 이름으로 사용
- 이전에는 필요한 객체를 `AppConfig`를 이용해 직접 조회했지만 이젠 스프링 컨테이너를 통해 스프링 빈(객체)를 찾음
- 스프링 빈은 `applicationContxt.getbean()`메서드를 이용해 찾을 수 있음
#### 스프링 컨테이너 사용 예시 코드
```java
// AppConfig
@Configuration
public class AppConfig {
    @Bean
    public MemberService memberService() { return new MemberServiceImpl(memberRepository()); }
}

// 스프링 빈 사용 코드
ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
```

---

## 스프링 컨테이너 생성
### 스프링 컨테이너?
- 스프링 컨테이너는 `BeanFactory`와 `ApplicationContext`로 구분
- `BeanFactory`를 직접 사용하는 경우는 거의 없음, 일반적으로 `ApplicationContext`스프링 컨테이너라 함
- 스프링 컨테이너는 `XML기반` 또는 `애노테이션 기반의 자바 설정 클래스`로 생성 가능
- AppConfig를 사용해 생성한 방식이 애노테이션 기반의 자바 설정 클래스로 생성한 것

#### 스프링 컨테이너 생성 예제 코드
```java
// 스프링 컨테이너 생성
ApplicationContext applicationContext = 
        new AnnotationConfigApplicationContext(AppConfig.class);
```
- `ApplicationContext`는 인터페이스
- `AnnotationConfigApplicationContext` 클래스는 `ApplicationContext` 인터페이스 구현체임

### 스프링 컨테이너 생성 과정
1. 스프링 컨테이너 생성
   - 스프링 컨테이너는 구성 정보 지정 필요, 구성 정보(`AppConfig`)를 매개변수로 넘겨줌
   - `new AnnotationConfigApplicationContext(AppConfig.class)`를 통해 컨테이너 생성
2. 스프링 빈 등록
   - 매개변수로 받은 구성 클래스 정보를 사용해 스프링 빈 등록 
     - `@Bean` 붙은 메서드 모두 호출
     - 스프링 빈 저장소에 메서드 명을 빈 이름으로 반환된 객체 빈 객체로 등록
   - 빈 이름은 직접 부여할 수 있지만 일반적으로 메서드 이름을 사용
   - `빈 이름끼리 중복❌` (다른 빈이 무시 또는 기존 빈을 덮어버리거나 오류 발생)
3. 스프링 빈 의존관계 설정
   - 스프링 컨테이너틑 구성 정보를 참고해 의존관계를 주입(DI)

---

## 컨테이너에 등록된 모든 빈 조회
### 모든 빈 출력하기
- `ac.getBeanDefinitionNames()` : 스프링에 등록된 모든 빈을 조회
- `ac.getBean()` : 빈 이름으로 빈 객체(인스턴스) 조회
### 애플리케이션 빈 출력하기
- 스프링이 내부에서 사용하는 빈 제외하고 사용자가 정의한 빈만 출력
- 사용자가 정의한 빈은 `ac.getRole()`로 구분해 출력할 수 있음
  - `ROLE_APPLICATION` : 직접 등록한 애플리케이션 빈
  - `ROLE_INFRASTRUCTURE` : 스프링이 내부에서 사용하는 빈

---

## 스프링 빈 조회
### 기본적인 조회 방법
- `ac.getBean(빈이름, 타입)`
- `ac.getBean(타입)`
- 조회 대상이 없으면 예외 발생 `NoSuchBeanDefinitionException`

### 동일한 타입이 둘 이상
- `ac.getBeansOfType()` : 해당 타입의 모든 빈 조회
- 같은 타입의 스프링 빈이 둘 이상이면 `NoUniqueBeanDefinitionException` 예외 발생 
  - 같은 타입의 빈이 둘 이상이면 빈 이름을 지정해 찾자

### 상속 관계
- 빈이 상속관계를 가지고 있을때 부모 타입으로 조회하면 자식 타입도 함께 조회
  - 자바 객체의 최고 부모인 `Object` 타입으로 조회하면 모든 스프링 빈을 조회할 수 있음

---

## BeanFactory와 ApplicationContext
### BeanFactory
- 스프링 컨테이너의 최상위 인터페이스
- 스프링 빈을 관리하고 조회하는 역할 담당
- `getBean()` 제공
- 사용했던 대부분의 기능은 BeanFactory가 제공하는 기능
- 직접 사용하는 일은 거의 없고 부가 기능이 포함된 ApplicationContext를 주로 사용

### ApplicationContext
```java
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
MessageSource, ApplicationEventPublisher, ResourcePatternResolver { }
```
- BeanFactory 기능을 모두 상속받아 제공
- ApplicationContext는 빈 관리기능 + 편리한 부가 기능
  - `MessageSource` : 메시지소스를 활용한 국제화 가능
  - `EnvironmentCapable` 환경변수 : 로컬, 개발, 운영 등을 구분해서 처리
  - `ApplicationEventPublisher` 애플리케이션 이벤트 : 이벤트 발행하고 구독하는 모델을 편리하게 지원
  - `ResourceLoader` 편리한 리소스 조회 : 파일, 클래스 패스, 외부 등에서 편리하게 조회 
  
--- 

## 스프링 컨테이너 XML 설정정보 만들기
- 스프링 컨테이너는 다양한 형식 설정 정보를 사용할 수 있게 유연하게 설계
  - 자바코드, XML, Groovy 등
  
#### 애노테이션 기반 자바 코드 설정
- `new AnnotationConfigApplicationContext(AppConfig.class)`
- `AnnotationConfigApplicationContext` 클래스 사용하면서 자바 코드로된 설정 정보를 넘김

#### XML 설정 사용
- 컴파일 없이 빈 설정 정보 변경 가능
- 스프링 부트를 많이 사용하면서 XML 기반 설정을 잘 사용하지 않음
- `GenericXmlApplicationContext` 클래스를 사용하면서 XML 설정 파일을 넘김

---

## BeanDefinition
- `BeanDefinition` : 빈 설정 메타정보
- `BeanDefinition` 이라는 추상화를 통해 스프링이 다양한 설정 형식을 지원할 수 있게함
  - `@Bean`, `<bean>`당 각각 하나씩 메타 정보를 생성
  - XML을 읽어 BeanDefinition을 만들거나, 자바 코드를 읽어 BeanDefinition을 만듦
- 스프링 컨테이너는 메타정보를 기반으로 스프링 빈을 생성

#### 코드레벨로 자세히 살펴보기
- `AnnotationConfigApplicationContext`는 `AnnotateBeanDefinitionReader`를 사용해 `AppConfig.class`를 읽고 `BeanDefinition`을 생성
- `GenericXmlApplicationContext`는 `XmlBeanDefinitionReader`를 사용해 `appConfig.xml`을 읽고 `BeanDefinition`을 생성
- 새로운 형식의 설정 정보가 추가되면 `XxxBeanDefinitionReader`를 만들어 `BeanDefinition`을 생성

### BeanDefinition 살펴보기
- `BeanClassName` : 생성할 빈의 클래스명 (자바 설정처럼 팩토리 역할의 빈을 사용하면 없음)
- `factoryBeanName` : 팩토리 역할의 빈을 사용할 경우 (appConfig)
- `factoryMethodName` : 빈을 생성할 팩토리 메서드 지정 (memberService)
- `Scope` : 싱글톤(기본값)
- `lazyInit` : 스프링 컨테이너를 생성할 때 빈을 생성하는 것이 아니라, 실제 빈을 사용할 때까지 최대한 생성이 지연처리하는지 여부
- `IniteMethodName` : 빈을 생성하고 의존관계를 적용한 뒤에 호출되는 초기화 메서드 명
- `DestroyMethodName` : 빈의 생명주기가 끝나서 제거하기 직전에 호출되는 메서드 명
- `Constructor arguments, Properties` : 의존관계 주입에서 사용 (자바 설정처럼 팩토리 역할의 빈을 사용하면 없음)

---

## 웹 애플리케이션과 싱글톤
- 웹 애플리케이션은 보통 여러 고객이 동시에 요청
- 현재 스프링없는 순수한 DI 컨테이너 AppConfig는 요청이 올 때마다 매번 새로운 객체를 생성
- 매번 새로운 객체를 생성하면 메모리 낭비가 심함
- 해당 객체를 1개만 생성하고 공유하도록 설계 👉`싱글톤 패턴`

---

## 싱글톤 패턴
- 클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴

#### 싱글톤 예제
- 구현방법은 여러가지가 있지만 객체를 미리 생성해두는 가장 단순하고 안전한 방법으로 예시
```java
public class SingletonService {

    // static 영역에 인스턴스 생성
    private static final SingletonService instance = new SingletonService();

    // 객체 인스턴스가 필요하면 이 static 메서드를 통해서만 조회하도록 허용
    public static SingletonService getInstance() {
        return instance;
    }

    // private 생성자통해 외부에서 객체 생성을 막음
    private SingletonService() {
    }
}
```
1. static 영역에 객체 instance를 하나 생성해 올려둔다
2. 객체 인스턴스를 사용할땐 `getInstance()`메서드를 통해서만 조회가능, 이 메서드는 항상 같은 instance 반환
3. 1개의 객체 인스턴스만 존재하기 위해 생성자를 `private`으로 막아 외부에서 `new` 키워드 객체 생성을 막음

### 싱글톤 패턴 문제점
- 싱글톤 패턴을 구현하는 코드 자체가 많이 들어감
- 의존관계상 클라이언트가 구체 클래스에 의존 👉DIP 위반
- 클라이언트가 구체 글래스에 의존해서 OCP 위반 가능성이 높음
- 테스트 어려움
- 내부 속성을 변경하거나 초기화 어려움
- private 생성자로 자식 클래스 생성 어려움
- 결론적으로 유연성이 떨어짐
- 안티패턴으로 불리기도 함

---

## 싱글톤 컨테이너
- 스프링 컨테이너는 싱글톤 패턴의 문제점을 해결하면서 객체 인스턴스를 싱글톤으로 관리함
- 스프링 빈이 싱글톤으로 관리되는 빈

### 싱글톤 컨테이너
- 스프링 컨테이너는 싱글톤 컨테이너 역할을 함, 싱글톤 패턴을 적용하지 않아도 싱글톤으로 관리함
  - 싱글톤 패턴을 위한 코드가 들어가지 않음
  - DIP, OCP, 테스트, private 생성자로부터 자유롭게 싱글톤 사용 가능
- 싱글톤 객체를 생성하고 관리하는 기능을 `싱글톤 레지스트리`라고 함
- 스프링의 기본 빈 등록 방식은 싱글톤이지만 요청할 때마다 새로운 객체 반환 기능도 제공함

---

## 싱글톤 방식의 주의점
- 싱글톤 방식은 여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문에 싱글톤 객체는 상태를 유지(stateful)하게 설계하면 안됨
- `무상태 stateless`하게 설계해야함
  - 특정 클라이언트에 의존적인 필드가 있으면 안됨
  - 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안됨
  - 가급적 읽기만 가능해야 함
  - 필드 대신에 자바에서 공유되지 않는 지역변수, 파라미터, ThreadLocal 등을 사용해야 함
- 스프링 빈(싱글톤 빈)의 필드에 공유값을 설정하면 큰 장애 발생할 수 있음

#### 상태를 유지할 경우 발생하는 문제점 예시
```java
public class StatefulService {

    private int price; // 공유되는 필드

    public void order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        this.price = price; // 여기가 문제 💡특정 클라이언트가 값을 변경
    }

    public int getPrice() {
        return price;
    }
}
```

---

## @Configuration과 싱글톤
- memberService 빈을 생성하는 코드를 보면 `memberRepository()` 호출
  - `new MemmoryMemberRepository()`가 호출
- orderService 빈을 생성하는 코드도 동일하게 `memberRepository()` 호출
  - `new MemmoryMemberRepository()`가 호출
- `MemmoryMemberRepository`가 2개 생성되면서 싱글톤이 깨지는 것처럼 보임
- 테스트코드로 검증해보면 싱글톤이 유지됨 -> 스프링 컨테이너가 해결

---

## @Configuration과 바이트코드 조작의 마법
- 스프링 컨테이너는 싱글톤 레지스트리, 스프링 빈이 싱글톤이 되도록 보장해줘야함
- 싱글톤을 보장하기 위해 스프링 클래스의 바이트코드를 조작하는 라이브러리를 사용

#### appConfig 클래스 정보 확인
```java
AppConfig appConfig = ac.getBean(AppConfig.class);
// 출력 결과 : class hello.core.AppConfig$$SpringCGLIB$$0
```
- `AnnotationConfigApplicationContext` 파라미터로 넘긴 설정 정보 `AppConfig`도 빈으로 등록됨
- 순수한 클래스라면 `class hello.core.AppConfig`라고 출력됨
- 결과는 `class hello.core.AppConfig$$SpringCGLIB$$0`
- 스프링이 `CGLIB`라는 바이트코드 조작 라이브러리를 사용해서 **AppConfig 클래스를 상속받은 임의의 다른 클래스를 생성**하고 빈으로 등록함
  - AppConfig@CGLIB는 AppConfig의 자식 타입으로 AppConfig 타입으로 조회 가능
- 이 임의의 클래스가 싱글톤을 보장


##### AppConfig@CGLIB 예상 코드

```java
@Bean
public MemberRepository memberRepository() {
    if(memoryMemberRepository 이미 스프링 컨테이너에 등록?){
        return 스프링 컨테이너에서 찾아서 반환
    } else {
        기존 로직 호출 MemoryMemberRepository 생성 후 스프링 컨테이너 등록
        return 반환
    }
}
```
- @Bean이 붙은 메서드마다 스프링 빈이 존재하면 존재하는 빈 반환, 없으면 생성 후 스프링 빈에 등록하여 반환하는 코드가 동적으로 생성
- 싱글톤을 보장해줌

#### @Configuration 없이 @Bean만 적용하면?
- 스프링 빈으로 등록은 되지만 싱글톤을 보장하지 않음

---

## 컴포넌트 스캔과 의존관계 자동주입 시작
- 설정해야할 빈의 갯수가 늘어나면 설정 정보를 작성하는 것도 어려워짐(누락, 반복 등)
- 그래서 스프링은 설정 정보가 없어도 자동으로 스프링 빈을 등록하는 `컴포넌트 스캔`을 제공
- 의존관계도 자동으로 주입하는 `@Autowired`라는 기능도 제공

#### @ComponentScan
```java
@Configuration
@ComponentScan (
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
) // 기존에 작성한 설정정보 스캔대상에서 제외
public class AutoAppConfig {
}
```
- 스프링 빈으로 등록해주기 위해선 각 클래스가 컴포넌트 스캔 대상이 되도록 `@Component` 애노테이션을 붙여야함
- `@Component` 애노테이션이 붙은 클래스를 스캔해 스프링 빈으로 등록
  - 빈 이름 기본 전략 : 클래스 명 맨 앞글자 소문자로 사용 MemberServiceImpl 👉 memberServiceImpl
  - 빈 이름 직접 지정 : @Component("memberService") 직접 이름을 부여
- `@Configuration`이 스캔 대상이 되는 이유는 `@Component` 애노테이션이 붙어있기 때문

#### @Autowired
```java
@Component
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    @Autowired // 자동 의존관계 주입
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

  ...
}
```
- 이전 설정정보를 작성할 때는 `@Bean`으로 직접 설정정보를 작성하고 의존관계도 직접 명시
- `@ComponentScan`을 사용하면 이런 설정정보가 없기 때문에 의존관계 주입도 클래스 안에서 해결해야함
- 자동으로 의존관계를 주입하기 위해 생성자에 `@Autowired` 애노테이션을 사용
- 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아 주입
  - 이때 기본 조회 전략을 타입이 같은 빈을 찾아서 주입
  - `getBean(MemberRepository.class)`와 동일하다고 이해

---
