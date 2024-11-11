package com.cpplab.security.handler;

import com.cpplab.domain.auth.dto.CustomOAuth2User;
import com.cpplab.domain.auth.entity.RefreshEntity;
import com.cpplab.domain.auth.repository.RefreshRepository;
import com.cpplab.security.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${fe.url}")
    private String feUrl;

    @Value("${spring.jwt.token.access-expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${spring.jwt.token.refresh-expiration-time}")
    private long refreshTokenExpirationTime;


    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        Long userId = customUserDetails.getUserId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성
        String access = jwtUtil.createJwt("access", userId, accessTokenExpirationTime);
        String refresh = jwtUtil.createJwt("refresh", userId, refreshTokenExpirationTime);

        //Refresh 토큰 저장
        addRefreshEntity(userId, refresh, refreshTokenExpirationTime);

        //응답 설정
        response.setHeader("access", access); // 응답헤더에 엑세스 토큰
        response.addCookie(createCookie("refresh", refresh)); // 응답쿠키에 리프레시 토큰
        //response.setStatus(HttpStatus.OK.value()); 추후 exception 코드로 변경
        response.sendRedirect(feUrl);
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
        cookie.setMaxAge((int)refreshTokenExpirationTime);
//        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
