package com.cpplab.domain.auth.service;


import com.cpplab.domain.auth.entity.RefreshEntity;
import com.cpplab.domain.auth.repository.RefreshRepository;
import com.cpplab.security.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${spring.jwt.token.access-expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${spring.jwt.token.refresh-expiration-time}")
    private long refreshTokenExpirationTime;

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ResponseEntity<?> reissueAccess(String refresh, HttpServletResponse response) {

        ResponseEntity<String> validationResponse = validateRefreshToken(refresh);
        if (validationResponse != null) {
            return validationResponse; // 에러가 있을 경우 반환
        }

        Long userId = jwtUtil.getUserId(refresh);

        // 새로운 JWT 생성
        String newAccess = jwtUtil.createJwt("access", userId, accessTokenExpirationTime);

        // 응답 설정
        response.setHeader("access", newAccess);
        System.out.println(newAccess);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> reissueTokens(String refresh, HttpServletResponse response) {

        ResponseEntity<String> validationResponse = validateRefreshToken(refresh);
        if (validationResponse != null) {
            return validationResponse; // 에러가 있을 경우 반환
        }

        Long userId = jwtUtil.getUserId(refresh);

        // 새로운 JWT 생성
        String newAccess = jwtUtil.createJwt("access", userId, accessTokenExpirationTime);
        String newRefresh = jwtUtil.createJwt("refresh", userId, refreshTokenExpirationTime);

        // DB에서 기존의 Refresh 토큰 삭제 후 새로운 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(userId, newRefresh, refreshTokenExpirationTime);

        // 응답 설정
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<String> validateRefreshToken(String refresh) {
        // expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        return null; // 유효성 검사를 통과한 경우 null 반환
    }

    private void addRefreshEntity(Long userId, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUserId(userId);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int)refreshTokenExpirationTime); // 24시간
        cookie.setHttpOnly(true);
        return cookie;
    }
}
