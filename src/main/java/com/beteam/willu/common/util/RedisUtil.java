package com.beteam.willu.common.util;

import com.beteam.willu.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    // Username - RefreshToken 형태로 저장
    private final StringRedisTemplate redisTemplate;
    // AccessToken - logout 형태로 저장
    private final StringRedisTemplate redisBlackListTemplate;
    private static final String KEY_PREFIX = "RT:";
    Set<String> keySet = new HashSet<>();

    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + username);
    }

    public void saveRefreshToken(String username, String refreshToken) {
        // ExpirationTime 설정을 통해 자동 삭제 처리
        redisTemplate.opsForValue().set(KEY_PREFIX + username, refreshToken, JwtUtil.REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public void deleteRefreshToken(String username) {
        redisTemplate.delete(KEY_PREFIX + username);
    }

    public void addBlackList(String accessToken, Long remainingTime) {
        redisBlackListTemplate.opsForValue()
                .set(accessToken, "logout", remainingTime, TimeUnit.MILLISECONDS);
    }

    public boolean isBlackList(String accessToken) {
        return redisBlackListTemplate.opsForValue().get(accessToken) != null;
    }
}