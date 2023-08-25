package com.beteam.willu.user.review.entity;

import com.beteam.willu.common.Timestamped;
import com.beteam.willu.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "reviews")
@NoArgsConstructor
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id") //리뷰 수신자-admin
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "sender_id") //리뷰 작성자-게스트
    private User sender;

    private String content;

    private long score;

    public Review(User receiver, User sender, String content, long score) {
        this.receiver = receiver;
        this.sender = sender;
        this.content = content;
        this.score = score;
    }
}
