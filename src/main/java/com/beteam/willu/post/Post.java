package com.beteam.willu.post;

import com.beteam.willu.common.Timestamped;
import com.beteam.willu.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "posts")
public class Post extends Timestamped {

    /**
     * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_name")
    private String title;

    @Column(name = "post_content")
    private String content;

//    private category 배열

    @Column(name = "post_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime promiseTime;

    @Column(name = "post_area")
    private String promiseArea;

    @Column(name = "post_maxnum")
    private Long maxnum; //모집 최대 인원
    /**
     * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
     */
    @ManyToOne
    @JoinColumn(name= "user_id")
    private User user;

    /**
     * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
     */
     public Post(PostRequestDto postRequestDto) {
         if (postRequestDto.getTitle() != null) this.title = postRequestDto.getTitle();
         if (postRequestDto.getContent() != null) this.content = postRequestDto.getContent();
         if (postRequestDto.getPromiseTime() != null) this.promiseTime = postRequestDto. getPromiseTime();
         if (postRequestDto.getPromiseArea() != null) this.promiseArea = postRequestDto.getPromiseArea();
         if (postRequestDto.getMaxnum()!= null) this.maxnum = postRequestDto.getMaxnum();
     }

    /**
     * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
     */
    /**
     * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
     */
    public void setUser(User user) {
        this.user = user;
    }

    public void update(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
        this.promiseTime = postRequestDto. getPromiseTime();
        this.promiseArea = postRequestDto.getPromiseArea();
        this.maxnum = postRequestDto.getMaxnum();
    }
}
