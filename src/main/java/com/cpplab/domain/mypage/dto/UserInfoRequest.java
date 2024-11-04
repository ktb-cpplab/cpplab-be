package com.cpplab.domain.mypage.dto;


public record UserInfoRequest(
        String nickName // 서비스 닉네임
        // 추후 유저 정보 수정이 커짐을 대비해 record로 제작
) {
}
