package com.cpplab.domain.auth.dto;

import com.cpplab.global.common.enums.Provider;

public interface OAuth2Response {
    //제공자 (Ex. naver, google, ...)
    Provider getProvider();
    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();
    //이메일
    String getEmail();
    //사용자 실명 (설정한 이름)
    String getNickName();
    //사용자 프로필 사진
    String getProfileImage();
}
