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
### 스프링 컨테이너 사용 예시 코드
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