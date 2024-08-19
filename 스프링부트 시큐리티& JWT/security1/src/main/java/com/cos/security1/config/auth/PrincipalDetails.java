package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {

    // 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행
    // 로그인 진행이 완료되면 시큐리티 session을 만들어줌(Security ContextHolder)
    // 시큐리티 session에 들어갈 수 있는 오브젝트 -> Authentication 객체
    // Authentication 안에 User 정보 필요
    // User 오브젝트 -> UserDetails 객체

    // Security session을 => Authentication => UserDetails(PrincipalDetails)

    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    // 해당 유저의 권한을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();

        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
