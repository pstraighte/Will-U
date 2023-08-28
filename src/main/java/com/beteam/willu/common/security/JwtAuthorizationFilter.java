package com.beteam.willu.common.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.beteam.willu.common.jwt.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String accessToken = jwtUtil.getTokenFromRequest(request, JwtUtil.AUTHORIZATION_HEADER);
		String refreshToken;
		//Access 토큰이 존재하면
		if (StringUtils.hasText(accessToken)) {
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
					accessToken = jwtUtil.reissue(refreshToken, response);
				}
			}

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
