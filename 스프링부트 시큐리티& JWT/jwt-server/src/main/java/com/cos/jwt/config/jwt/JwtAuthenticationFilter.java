package com.cos.jwt.config.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// /login으로 로그인을 요청하면(post) UsernamePasswordAuthenticationFilter 동작함
// 현재 securityConfig에서 formlogin을 disable했기 때문에 동작안함
// 다시 동작하게 하려면 해당 필터를 다시 등록하는 과정이 필요
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    // /login 요청을하면 로그인 시도를 위해 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        System.out.println("JwtAuthenticationFilter= 로그인 시도");

        // 1. username, pw 받아서

        // 2. 정상인지 로그인 시도 해보기
        // authenticationManager로 로그인 시도하면
        // PrincipalDetailsService loadUserByUsername 함수 실행


        // 3. PrincipalDetails를 세션에 담고
        // 권한관리를 위해 세선에 담음


        // 4. JWT 토큰을 생성해 응답


        return super.attemptAuthentication(request, response);
    }
}
