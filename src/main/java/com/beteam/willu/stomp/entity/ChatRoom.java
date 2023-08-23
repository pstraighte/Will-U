package com.beteam.willu.stomp.entity;

import com.beteam.willu.common.Timestamped;
import com.beteam.willu.post.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "post_id")
    private Post post;

    // 방이름
    @Column(name = "roomName")
    private String roomName;

    // 채팅방 제목
    @Column(name = "chatTitle")
    private String chatTitle;

    // 상태
    @Column(name = "activated")
    private boolean activated;

    // 게시물 삭제시 해당 채팅방, 채팅메시지, 채팅유저 삭제
    @OneToMany(mappedBy = "chatRooms", cascade = CascadeType.REMOVE)
    private List<UserChatRoom> userChatRoomList = new ArrayList<>();

    @OneToMany(mappedBy = "chatRooms", cascade = CascadeType.REMOVE)
    private List<Chat> chatList = new ArrayList<>();

}
