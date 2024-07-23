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
  ```
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

    public MemberService memberService(){
        return new MemberServiceImpl(memberRepository());
    }
    
    private MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    } // memberRepository 구현을 따로 메서드로 빼줌

- 기존 코드 ➡️ memberService, orderService에 `new MemoryMemberRepository()` 중복이 있고, 역할에 따른 구현이 잘 안보임 
- 리팩토링 ➡️ 역할과 구현 클래스를 분리해 애플리케이션 전체 구성이 어떻게 되어있는지 빠르게 파악 가능

---
## 새로운 구조와 할인 정책 적용
- `AppConfig` 등장 ➡️`사용영역`과 `구성영역`으로 분리됨
- OCP, DIP 객체지향 설계 원칙을 준수? ⭕
  - DIP : 주문 서비스 클라이언트는 DiscountPolicy 인터페이스에만 의존, 구체(구현) 클래스는 주입받음
  - OCP : 기능을 확장, 변경시 클라이언트 코드 수정 불필요, AppConfig에서 모두 해결