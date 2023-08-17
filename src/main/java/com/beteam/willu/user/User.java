package com.beteam.willu.user;

import com.beteam.willu.common.Timestamped;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // 아이디
    @Column(name = "username", nullable = false, unique = true, length = 40)
    private String username;

    // 비밀번호
    @Column(name = "password", nullable = false)
    private String password;

    // 닉네임
    @Column(name = "nickname", nullable = false, unique = true, length = 40)
    private String nickname;

    // 번호
    @Column(name = "phoneNumber")
    private String phoneNumber;

    // 지역
    @Column(name = "area")
    private String area;

    // 프로필 사진 url
    @Column(name = "picture")
    private String picture;

    // 기본 점수
    @Column(name = "score", nullable = false)
    @Builder.Default
    private Long score = 3L;

    @Transactional
    public void profileUpdate(UserUpdateRequestDto updateRequestDto) {
        this.username = updateRequestDto.getUsername();
        this.nickname = updateRequestDto.getNickname();
        this.phoneNumber = updateRequestDto.getPhoneNumber();
        this.area = updateRequestDto.getArea();
        this.picture = updateRequestDto.getPicture();
    }
}

