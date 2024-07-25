package hello.core.singleton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.*;

class StatefulServiceTest {

    @Test
    @DisplayName("상태를 유지하는 필드의 문제점")
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

        StatefulService statefulService1 = ac.getBean(StatefulService.class);
        StatefulService statefulService2 = ac.getBean(StatefulService.class);

        statefulService1.order("userA", 10000); // ThreadA : A 사용자 10000원 주문
        statefulService2.order("userB", 20000); // ThreadA : B 사용자 20000원 주문

        // ThreadA : A 사용자 금액 조회
        int price1 = statefulService1.getPrice();
        System.out.println("price1 = " + price1); // 10000원이 아닌 20000원 출력
        assertThat(statefulService1.getPrice()).isEqualTo(20000);

    }


    static class TestConfig {
        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }
}