package com.cos.jwt.controller;

import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("home")
    public String home() {
        return "<h1>home</h1>";
    }

    @PostMapping("token")
    public String token() {
        return "<h1>token</h1>";
    }

    @PostMapping("/join")
    public String join(User user) {
        user.setRoles("ROLE_USER");
        String encPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);

        userRepository.save(user);

        return "redirect:/loginForm";
    }
}