package com.beteam.willu.stomp.entity;

import com.beteam.willu.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "Chats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Chat_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chatRooms_id")
    private ChatRoom chatRooms;

    // 채팅 내용
    @Column(name = "chatContent")
    private String chatContent;

    // 채팅 저장 시점 내용
    @Column(name = "created_date")
    private LocalDateTime createdAt;

}
