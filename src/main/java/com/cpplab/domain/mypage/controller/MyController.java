package com.cpplab.domain.mypage.controller;

import com.cpplab.domain.auth.dto.CustomOAuth2User;
import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.mypage.dto.PortfolioRequest;
import com.cpplab.domain.mypage.dto.UserInfoRequest;
import com.cpplab.domain.mypage.entity.PortfolioEntity;
import com.cpplab.domain.mypage.service.MyService;
import com.cpplab.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MyController {

    private final MyService myService;

    // 회원정보 조회
    @GetMapping("/my")
    public ApiResponse<UserEntity> getUserInfo(@AuthenticationPrincipal CustomOAuth2User customUser){
        return ApiResponse.onSuccess(myService.getUserInfo(customUser.getUserId()));
    }

    // 회원정보 수정
    @PutMapping("/my")
    public ApiResponse<UserEntity> putUserInfo(@AuthenticationPrincipal CustomOAuth2User customUser, @RequestBody UserInfoRequest request){
        return ApiResponse.onSuccess(myService.putUserInfo(customUser.getUserId(), request));
    }


    // 나의 포트폴리오 조회
    @GetMapping("/portfolio")
    public ApiResponse<PortfolioEntity> getPortfolio(@AuthenticationPrincipal CustomOAuth2User customUser){
        return ApiResponse.onSuccess(myService.getPortfolio(customUser.getUserId()));
    }

    // 나의 포트폴리오 수정, 추후 patch수정
    @PutMapping("/portfolio")
    public ApiResponse<PortfolioEntity> putPortfolio(@AuthenticationPrincipal CustomOAuth2User customUser, @RequestBody PortfolioRequest request){
        return ApiResponse.onSuccess(myService.putPortfolio(customUser.getUserId(), request));
    }

}
