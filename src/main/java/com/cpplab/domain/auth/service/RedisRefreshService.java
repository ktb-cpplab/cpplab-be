package com.cpplab.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisRefreshService {

    private final RedisTemplate<String, String> redisTemplate;

    // 리프레시 토큰 저장 (TTL 설정)
    public void saveRefreshToken(Long id, String refreshToken, long expirationTimeInMillis) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(String.valueOf(id), refreshToken, expirationTimeInMillis, TimeUnit.MILLISECONDS);
//        valueOperations.set(refreshToken, String.valueOf(id), expirationTimeInMillis, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String userName) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(userName);
    }

    public void deleteRefreshToken(Long id) {
        log.info(String.valueOf(id));
        redisTemplate.delete(String.valueOf(id));
    }

    public boolean hasValidRefreshToken(String userName, String refreshToken) {
        String storedToken = getRefreshToken(userName);
        return storedToken != null && storedToken.equals(refreshToken);
    }

}
