package com.beteam.willu.common.jwt;

import com.beteam.willu.common.redis.RedisUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j(topic = "jwtUtil")
@Component
public class JwtUtil {
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "RT";

    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";

    // Access 토큰 만료시간
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 30 * 1000L; // 2분(재발급확인용)

    // Refresh 토큰 만료 시간
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; // 3일

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final RedisUtil redisUtil;
    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");


    public JwtUtil(RedisUtil redisUtil, @Value("${jwt.secret.key}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisUtil = redisUtil;
    }

    // 토큰 생성
    public String createRefreshToken(String username) {
        Date date = new Date();
        return BEARER_PREFIX
                + Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_EXPIRE_TIME)) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();
    }

    //access token 만 생성 -> refresh 토큰 요청이 왔을때 사용됨
    public String createAccessToken(String username) {
        Date date = new Date();
        return BEARER_PREFIX
                + Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRE_TIME)) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();
    }

    // JWT Cookie 에 저장
    public void addJwtToCookie(String token, String header, HttpServletResponse res) {
        token = URLEncoder.encode(token, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

        Cookie cookie = new Cookie(header, token); // Name-Value
        cookie.setPath("/");

        // Response 객체에 Cookie 추가
        res.addCookie(cookie);
    }

    // JWT 토큰 substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            //블랙리스트에 들어있는지 확인
            if (redisUtil.isBlackList(token)) {
                throw new IllegalArgumentException("로그아웃된 토큰입니다");
            }
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new IllegalArgumentException("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            return false;
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
    }

    // HttpServletRequest 에서 Cookie Value : JWT 가져오기
    public String getTokenFromRequest(HttpServletRequest req, String cookieName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8)
                            .substring(7); // Encode 되어 넘어간 Value 다시 Decode
                }
            }
        }
        return null;
    }

    public String reissue(String refreshToken, HttpServletResponse response) {
        String username = getUserInfoFromToken(refreshToken).getSubject();
        // cookie 에서 가져온 refresh token 과 redis 의 refresh token 비교
        String redisRT = redisUtil.getRefreshToken(username);
        if (!StringUtils.hasText(redisRT)) {
            throw new NullPointerException(username + " 에 해당하는 redis refresh token 존재하지 않습니다. 다시 로그인하세요.");
        }
        if (!refreshToken.equals(redisRT.substring(7))) {
            throw new IllegalArgumentException("refresh 토큰이 일치하지 않습니다.");
        }
        //재발급 진행
        String newAccessToken = createAccessToken(username);
        expireCookie(response, JwtUtil.AUTHORIZATION_HEADER);
        addJwtToCookie(newAccessToken, JwtUtil.AUTHORIZATION_HEADER, response);
        return newAccessToken.substring(7);

    }

    //jwt 토큰의 남은 유효시간
    public Long getExpiration(String accessToken) {
        Date expiration = getUserInfoFromToken(accessToken).getExpiration();
        long now = new Date().getTime();
        return expiration.getTime() - now;
    }

    public void expireCookie(HttpServletResponse response, String cookieHeader) {
        Cookie cookie = new Cookie(cookieHeader, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
