package com.beteam.willu.socialLogin;

import com.beteam.willu.security.JwtUtil;
import com.beteam.willu.user.User;
import com.beteam.willu.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "NAVER Login")
@Service
@RequiredArgsConstructor
public class SocialNaverService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    public void naverLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);


        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
       NaverUserInfoDto naverUserInfoDto = getNaverUserInfo(accessToken);

        //3, 필요시 회원가입
        User naverUser = registerNaverUserIfNeeded(naverUserInfoDto);

        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createAccessToken(naverUser.getUsername());

        // 쿠키 저장
        jwtUtil.addJwtToCookie(createToken, response);

    }

    private String getToken(String code) throws JsonProcessingException {
        log.info("인가코드 : " + code);
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://nid.naver.com")
                .path("/oauth2.0/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "0St_ZZCyosLX1RyQHKhs");
        body.add("client_secret", "_IXRyVOVgJ");
        body.add("redirect_uri", "http://localhost:8080/api/users/naver/callback");
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        System.out.println("naverjsonNode = " + jsonNode);
        return jsonNode.get("access_token").asText();
    }

    private NaverUserInfoDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
        log.info("accessToken : " + accessToken);
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/nid/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        System.out.println("jsonNode = " + jsonNode);
        String id = jsonNode.get("response").get("id").asText();
        String nickname = jsonNode.get("response")
                .get("name").asText();
        String email = jsonNode.get("response")
                .get("email").asText();

        log.info("네이버 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new NaverUserInfoDto(id, nickname, email);
    }

    private User registerNaverUserIfNeeded(NaverUserInfoDto naverUserInfo) {
        // DB 에 중복된 네이버 Id 가 있는지 확인 (네이버로 이미 로그인을 했었던 상태)
        String naverId = naverUserInfo.getId();
        User naverUser = userRepository.findByNaverId(naverId).orElse(null);

        if (naverUser == null) {
            // 카카오 id가 없는 사용자로 해당 사이트에서 제공하는 방법으로 회원가입한 사용자거나 신규 회원인 경우

            // 카카오 사용자 email 과 동일한 email 가진 회원이 있는지 확인 (해당 사이트에서 제공하느 방법으로 회원 가입한 사용자)
            String naverEmail = naverUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(naverEmail).orElse(null);
            if (sameEmailUser != null) {
                naverUser = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                naverUser = naverUser.naverIdUpdate(naverId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email
                String email = naverUserInfo.getEmail();
                String userNickname = naverUserInfo.getNickname();
                naverUser = User.builder().username(email).nickname(userNickname).password(encodedPassword).email(email).naverId(naverId).build();
            }

            userRepository.save(naverUser);
        }

        // 이미 카카오 id가 있는 유저 - 바로 쿠키를 저장할 데이터를 보내준다.

        return naverUser;
    }
}

