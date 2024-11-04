package com.cpplab.domain.auth.service;


import com.cpplab.domain.auth.dto.*;
import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.auth.repository.UserRepository;
import com.cpplab.domain.mypage.entity.PortfolioEntity;
import com.cpplab.domain.mypage.repository.PortfolioRepository;
import com.cpplab.global.common.enums.Rank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

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
        UserEntity existData = userRepository.findByProviderAndEmail(oAuth2Response.getProvider(), oAuth2Response.getEmail());

        if (existData == null){
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(oAuth2Response.getEmail()); // ex) tiger1650@naver.com
            userEntity.setProvider(oAuth2Response.getProvider()); // KAKAO
            userEntity.setName(oAuth2Response.getname()); // ex) 이용우
            userEntity.setNickName(oAuth2Response.getname()); // ex) 이용우
            userEntity.setProfileImage(oAuth2Response.getProfileImage()); // ex) 프로필 이미지

            userRepository.save(userEntity);

            // PortfolioEntity 생성 및 기본값 설정
            PortfolioEntity portfolioEntity = new PortfolioEntity();
            portfolioEntity.setUser(userEntity);
            portfolioEntity.setRank(Rank.NOT_EXIST); // 기본값으로 NOT_EXIST 설정

            portfolioRepository.save(portfolioEntity);


            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(userEntity.getUserId());
            userDTO.setEmail(userEntity.getEmail());
            userDTO.setName(userEntity.getName());

            return new CustomOAuth2User(userDTO);
        }
        else { // 이미 존재한다면
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(existData.getUserId());
            userDTO.setName(existData.getName());
            userDTO.setEmail(existData.getEmail());

            return new CustomOAuth2User(userDTO);
        }
    }

}
