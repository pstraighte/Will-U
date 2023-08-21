package com.beteam.willu.stomp.entity;

import com.beteam.willu.common.Timestamped;
import com.beteam.willu.post.Post;
import com.beteam.willu.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "ChatRooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatRoom_id")
    private Long id;

    @OneToOne
    @JoinColumn(name= "post_id")
    private Post post;

    // 방이름
    @Column(name = "roomName")
    private String roomName;

    // 상태
    @Column(name = "activated")
    private boolean activated;

}
