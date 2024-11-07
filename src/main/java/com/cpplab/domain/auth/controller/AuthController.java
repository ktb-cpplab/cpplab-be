package com.cpplab.domain.auth.controller;


import com.cpplab.domain.auth.service.AuthService;
import com.cpplab.global.common.ApiResponse;
import com.cpplab.global.common.code.status.ErrorStatus;
import com.cpplab.global.common.exception.GeneralException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/access")
    @Operation(summary = "엑세스 토큰 발급", description = "로그인 성공 후 바로 작동")
    public ApiResponse<Map<String, Object>> access(HttpServletRequest request, HttpServletResponse response) {

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {  // cookies가 null이 아닌지 확인
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }
        if (refresh == null) {
            throw new GeneralException(ErrorStatus.TOKEN_NOT_FOUND);
        }

        ResponseEntity<?> responseEntity = authService.reissueAccess(refresh, response);

        // Wrap result in ApiResponse and return
        Map<String, Object> result = (Map<String, Object>) responseEntity.getBody();
        return ApiResponse.onSuccess(result);

    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            throw new GeneralException(ErrorStatus.TOKEN_NOT_FOUND);
        }
        return authService.reissueTokens(refresh, response);
    }
}
