package com.beteam.willu.security;

import com.beteam.willu.common.util.RedisUtil;
import com.beteam.willu.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserDetailsServiceImpl userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtUtil.getTokenFromRequest(request, JwtUtil.AUTHORIZATION_HEADER);
        String refreshToken = null;
        //Access 토큰이 존재하면
        if (StringUtils.hasText(accessToken)) {
            // JWT 토큰 추출
            //accessToken = jwtUtil.substringToken(accessToken);
            log.info(accessToken);

            // access token 이 유효하지 않을 때
            if (!jwtUtil.validateToken(accessToken)) {
                log.info("access token 만료");
                //refresh token request cookie 에서 가져오기
                refreshToken = jwtUtil.getTokenFromRequest(request, JwtUtil.REFRESH_TOKEN_HEADER);
                if (!StringUtils.hasText(refreshToken)) {
                    throw new IllegalArgumentException("refresh 토큰이 존재하지 않습니다.");
                }
                //가져온 토큰 검증
                if (jwtUtil.validateToken(refreshToken)) {
                    String username = jwtUtil.getUserInfoFromToken(refreshToken).getSubject();
                    // cookie 에서 가져온 refresh token 과 redis 의 refresh token 비교
                    String redisRT = redisUtil.getRefreshToken(username);
                    if (!StringUtils.hasText(redisRT)) {
                        throw new NullPointerException(username + " 에 해당하는 redis refresh token 존재하지 않습니다. 다시 로그인하세요.");
                    }
                    if (!refreshToken.equals(redisRT.substring(7))) {
                        throw new IllegalArgumentException("refresh 토큰이 일치하지 않습니다.");
                    }
                    //재발급 진행
                    String newAccessToken = jwtUtil.createAccessToken(username);
                    jwtUtil.expireCookie(response, JwtUtil.AUTHORIZATION_HEADER);
                    jwtUtil.addJwtToCookie(newAccessToken, JwtUtil.AUTHORIZATION_HEADER, response);
                    accessToken = newAccessToken.substring(7);

                }
            }
            //검증 후 access token 재발급
/*            String newAccessToken = jwtUtil.reissueAccessToken(accessToken);
            if (newAccessToken != null) {
                jwtUtil.expireCookie(response, JwtUtil.AUTHORIZATION_HEADER);
                jwtUtil.addJwtToCookie(newAccessToken, response);
                log.info("새 토큰 쿠키에 추가" + newAccessToken);
                accessToken = jwtUtil.substringToken(newAccessToken);
            }*/
            log.info(jwtUtil.getExpiration(accessToken).toString());
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