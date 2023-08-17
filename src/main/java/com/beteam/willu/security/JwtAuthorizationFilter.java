package com.beteam.willu.security;

import com.beteam.willu.exception.BlackListedTokenException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisTemplate redisTemplate;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, RedisTemplate redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String tokenValue = jwtUtil.getTokenFromRequest(request);
        try {
            if (StringUtils.hasText(tokenValue)) {
                // JWT 토큰 substring
                tokenValue = jwtUtil.substringToken(tokenValue);
                log.info(tokenValue);

                //로그아웃된 토큰인지 검사
                validBlackToken(tokenValue);

                if (!jwtUtil.validateToken(tokenValue)) {
                    log.error("Token Error");
                    return;
                }

                Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

                setAuthentication(info.getSubject());

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    //레디스에 존재하는 토큰인지 확인
    private void validBlackToken(String accessToken) {
        //Redis 에 있는 엑세스 토큰인 경우 로그아웃 처리된 엑세스 토큰임.
        String blackToken = (String) redisTemplate.opsForValue().get(accessToken);
        log.info(blackToken);
        if (StringUtils.hasText(blackToken))
            throw new BlackListedTokenException("로그아웃 처리된 엑세스 토큰입니다.");
    }

}