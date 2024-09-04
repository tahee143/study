package com.cos.jwt.config;

import com.cos.jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.jwt.filter.MyFilter3;
import com.cos.jwt.repository.UserRepository;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CorsConfig corsConfig;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean // authenticationManager를 IoC에 등록해줌.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class); // 시큐리티 전에 filter3 동작

        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(sc -> sc.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않음
                .addFilter(corsConfig.corsFilter()) // @CrossOrigin(인증X), 시큐리티 필터에 등록 인증(O) --> 모든 요청 허용
                .formLogin((form)-> form.disable())
                .httpBasic((basic)-> basic.disable())
                .addFilter((Filter) new MyCustomDsl())
                *//* --------- security 최신 버전에서는 권한 적용시 ROLE_ 쓰지 않음. 즉, USER, ADMIN, MANAGER로 써야함 ---------- *//*
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/user/**").authenticated() // /user라는 url로 들어오면 인증이 필요하다.
                        .requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN") // manager으로 들어오는 MANAGER 인증 또는 ADMIN인증이 필요
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // /admin으로 들어오면 ADMIN권한이 있는 사람만 들어올 수 있음
                        .anyRequest().permitAll() // 그리고 나머지 url은 전부 권한을 허용
                );
        return http.build();
*/
        AuthenticationManager authenticationManager =  http.getSharedObject(AuthenticationManager.class);

        http.csrf(CsrfConfigurer::disable);
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilter(corsConfig.corsFilter()); // @CrossOrigin(인증X), 시큐리티 필터에 등록인증(O)
        http.formLogin((form) -> form.disable());
        http.httpBasic((basic) -> basic.disable());
        http.addFilter(new JwtAuthenticationFilter(authenticationManager)); // AuthenticationManger
        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/user/**").authenticated()
                .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/admin/**")
                .hasAnyRole("ADMIN").anyRequest().permitAll());
        return http.build();


    }
}
