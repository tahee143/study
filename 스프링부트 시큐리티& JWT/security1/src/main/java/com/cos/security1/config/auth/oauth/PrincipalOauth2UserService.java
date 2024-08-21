package com.cos.security1.config.auth.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest: " + userRequest);
        System.out.println("getClientRegistration: " + userRequest.getClientRegistration()); // registrationId로 어떤 OAuth로 로그인 했는지 확인 가능

        System.out.println("getAccessToken: " + userRequest.getAccessToken());
        System.out.println("getTokenValue: " + userRequest.getAccessToken().getTokenValue());

        // 구글 로그인 클릭 -> 구글 로그인 완료 -> code 반환 -> oauth client 라이브러리가 받음 -> AccessToken 요청까지가 userRequest
        // userRequest 정보 -> loadUser 함수 호출 -> 구글로부터 회원 프로필 정보 받아줌
        System.out.println("getAttributes: " + super.loadUser(userRequest).getAttributes());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        return oAuth2User;
    }
}
