package com.beteam.willu.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByEmail(String email);

    // 유저가 카카오 로그인을 한지 확인을 위함
    Optional<User> findByKakaoId(Long kakaoId);

    // 유저가 구글 로그인을 한지 확인을 위함
    Optional<User> findByGoogleId(String googleId);

    // 유저가 구글 로그인을 한지 확인을 위함
    Optional<User> findByNaverId(String naverId);

}
