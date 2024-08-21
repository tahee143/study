package com.cos.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize/postAuthorize 어노테이션 활성화
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeHttpRequests()
                .requestMatchers("/user/**").authenticated() // /user라는 url로 들어오면 인증이 필요하다.
                .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER") // /manager으로 들어오는 MANAGER 인증 또는 ADMIN인증이 필요하다는 뜻이다.
                .requestMatchers("/admin/**").hasRole("ADMIN") // //admin으로 들어오면 ADMIN권한이 있는 사람만 들어올 수 있음
                .anyRequest().permitAll() // 그리고 나머지 url은 전부 권한을 허용해준다.
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login") // /login 호출되면 시큐리티가 낚아채서 대신 로그인 진행
                .defaultSuccessUrl("/") // 성공하면 /로 이동
                .and()
                .oauth2Login()
                .loginPage("/loginForm");
        // 구글 로그린이 완료된 뒤의 후처리 필요


        return http.build();
    }

}
