package com.cos.security1.config.auth.oauth;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.auth.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.auth.oauth.provider.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

//    @Autowired // 순환참조 오류
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        /*
        System.out.println("userRequest: " + userRequest);
        System.out.println("getClientRegistration: " + userRequest.getClientRegistration()); // registrationId로 어떤 OAuth로 로그인 했는지 확인 가능

        System.out.println("getAccessToken: " + userRequest.getAccessToken());
        System.out.println("getTokenValue: " + userRequest.getAccessToken().getTokenValue());
        */

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 구글 로그인 클릭 -> 구글 로그인 완료 -> code 반환 -> oauth client 라이브러리가 받음 -> AccessToken 요청까지가 userRequest
        // userRequest 정보 -> loadUser 함수 호출 -> 구글로부터 회원 프로필 정보 받아줌
//        System.out.println("getAttributes: " + oAuth2User.getAttributes());

        // 회원가입 진행
        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            
        } else {
            System.out.println("구글과 페이스북만 지원");
        }

        String provider = oAuth2UserInfo.getProvider(); // google
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId; //google_10230123019283
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if(userEntity == null) {
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
