package com.beteam.willu.stomp.entity;

import com.beteam.willu.common.Timestamped;
import com.beteam.willu.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "Chats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Chat_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name= "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name= "chatRooms_id")
    private ChatRoom chatRooms;

    // 채팅 내용
    @Column(name = "chatContent")
    private String chatContent;

}
