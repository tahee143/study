package com.cos.jwt.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter3 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if(req.getMethod().equals("POST")) {
            System.out.println("POST 요청");
            String headerAuth = req.getHeader("Authorization");
            System.out.println("headerAuth = " + headerAuth);
            System.out.println("필터3");

            if (headerAuth.equals("hello")) {
                chain.doFilter(request, response);
            } else {
                PrintWriter out = res.getWriter();
                out.println("인증안됨");
            }
        }

        // 토큰을 만들어줘야함
        // id, pw 정상적으로 들어와 로그인이 완료되면 토큰을 생성해주고 응답
        // 요청할 때마다 Authorization value 값으로 토큰을 가져옴
        // 토큰이 내가 만든 토큰이 맞는지 검증작업 필요(RSA, HS256)
    }

}
