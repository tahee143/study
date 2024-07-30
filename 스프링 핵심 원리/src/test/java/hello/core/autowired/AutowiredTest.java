package hello.core.autowired;

import hello.core.member.Member;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;

class AutowiredTest {

    @Test
    @DisplayName("자동 주입 옵션 테스트")
    void AutowiredOption() {

        ApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class);
    }

    static class TestBean {

        // 기본값 true일때는 UnsatisfiedDependencyException 예외 발생
/*
        @Autowired
        public void setNoBean1(Member noBean1) {
            System.out.println("noBean1 = " + noBean1);
        }
*/

        // 테스트위해 스프링 빈이 아닌 Member 사용
        @Autowired(required = false)
        public void setNoBean1(Member noBean1) {
            System.out.println("noBean1 = " + noBean1);
        } // 자동 주입 대상이 없으면 수정자 메서드 자체가 실행이 안됨

        @Autowired
        public void setNoBean2(@Nullable Member noBean2) {
            System.out.println("noBean2 = " + noBean2);
        } // 자동 주입 대상이 없으면 null

        @Autowired
        public void setNoBean3(Optional<Member> noBean3) {
            System.out.println("noBean3 = " + noBean3);
        } // 자동 주입 대상이 없으면 Optional.empty

    }
}
