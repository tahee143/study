package com.cos.security1.controller;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.repository.UserRepository;
import com.cos.security1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {
        System.out.println("/test/login ==============");

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal(); // 다형성 다운 캐스팅
        System.out.println("principalDetails getUsername: " + principalDetails.getUser());

        System.out.println("userDetails: " + userDetails.getUser());
        // 두방법 모두 같은 user 정보

        return "세션 정보 확인하기";
    }

    // 세션속 스프링 시큐리티 세션 -> Authentication 객체만 들어갈 수 있음 -> Authentication안에는 OAuth2User,  객체 두개만 가능
    // UserDetails 일반 로그인, OAuth2User 소셜 로그인
    // 로그인 방법에 따라 처리 방법이 복잡
    // 이를 해결하기 위해 PrincipalDetails에 OAuth2User도 implement

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOauthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth) {
        System.out.println("/test/oauth/login ==============");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); // 다운 캐스팅
        System.out.println("oAuth2User getAttributes: " + oAuth2User.getAttributes());

        System.out.println("oAuth: " + oAuth.getAttributes());
        // 두방법 모두 같은 user 정보

        return "OAuth 세션 정보 확인하기";
    }

    @GetMapping({"", "/"})
    public String index() {
        return "index";
    }

    // OAuth 로그인해도 PrincipalDetails
    // 일반 로그인해도 PrincipalDetails로 받을 수 있음
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails userDetails) {
        System.out.println("PrincipalDetails: " + userDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        user.setRole("USER");
        String encPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);

        userRepository.save(user);

        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "데이터정보";
    }

}
