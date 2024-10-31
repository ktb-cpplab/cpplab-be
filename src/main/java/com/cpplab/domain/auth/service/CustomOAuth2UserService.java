package com.cpplab.domain.auth.service;


import com.cpplab.domain.auth.dto.*;
import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.auth.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest); // 유저 정보
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        Optional<UserEntity> existData = userRepository.findByUserName(username);

        if (existData.isPresent()){
            UserEntity userEntity = existData.get(); // Optional에서 UserEntity 추출
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(userEntity.getUserName());
            userDTO.setName(userEntity.getName()); // 혹은 nickname
            userDTO.setEmail(userEntity.getEmail());
            userDTO.setRole(userEntity.getRole());
            userDTO.setRole(userEntity.getRole());
            return new CustomOAuth2User(userDTO);
        }
        else {
            UserEntity userEntity = new UserEntity();
            userEntity.setUserName(username); // ex) kakao 3664463254
            userEntity.setEmail(oAuth2Response.getEmail()); // ex) tiger1650@naver.com
            userEntity.setName(oAuth2Response.getName()); // ex) 이용우
            userEntity.setImage(oAuth2Response.getImage()); // ex) 프로필 이미지
            userEntity.setRole("USER");

            userRepository.save(userEntity);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole("USER");

            return new CustomOAuth2User(userDTO);
        }
    }

}
