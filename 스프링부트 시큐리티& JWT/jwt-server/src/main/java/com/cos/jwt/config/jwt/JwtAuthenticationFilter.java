package com.cos.jwt.config.jwt;

import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;

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
        try {
//            BufferedReader br = request.getReader();
//
//            String input = null;
//            while ((input = br.readLine()) != null) {
//                System.out.println("input: " + input);
//            }

            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);
            System.out.println("user = " + user);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // PrincipalDetails의 loadUserByUsername() 함수 실행
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // authentication 객체가 session 영역에 저장 -> 로그인이 되었다는 의미
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("principalDetails = " + principalDetails.getUser().getUsername());

            return authentication;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 2. 정상인지 로그인 시도 해보기
        // authenticationManager로 로그인 시도하면
        // PrincipalDetailsService loadUserByUsername 함수 실행


        // 3. PrincipalDetails를 세션에 담고
        // 권한관리를 위해 세선에 담음


        // 4. JWT 토큰을 생성해 응답

    }
}
