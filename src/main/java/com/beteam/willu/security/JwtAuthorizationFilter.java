package com.beteam.willu.security;

import com.beteam.willu.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtUtil.getTokenFromRequest(request);

        //Access 토큰이 존재하면
        if (StringUtils.hasText(accessToken)) {
            // JWT 토큰 substring
            accessToken = jwtUtil.substringToken(accessToken);
            log.info(accessToken);
            //유효한지 검사해서 만료됐을 경우 재발급
            String newAccessToken = jwtUtil.reissueAccessToken(accessToken);
            if (newAccessToken != null) {
                jwtUtil.addJwtToCookie(newAccessToken, response);
                log.info("새 토큰 헤더에 추가" + newAccessToken);
                accessToken = newAccessToken;
            }

            if (!jwtUtil.validateToken(accessToken)) {
                log.info("액세스 토큰 유효하지 않음");
                return;
            }
            //기존의 유효토큰이나 재발급된 토큰의 정보 추출
            Claims info = jwtUtil.getUserInfoFromToken(accessToken);
            try {
                log.info(info.getSubject());
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.info("오류 발생");
                return;
            }
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


}