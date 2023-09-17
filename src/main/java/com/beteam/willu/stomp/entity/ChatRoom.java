package com.beteam.willu.stomp.entity;

import com.beteam.willu.common.Timestamped;
import com.beteam.willu.post.entity.Post;
import com.beteam.willu.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "chat_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    // 채팅방 제목
    @Column(name = "chat_title")
    private String chatTitle;

    // 상태
    @Column(name = "activated")
    private boolean activated;

    // 게시물 삭제시 해당 채팅방, 채팅메시지, 채팅유저 삭제
    @OneToMany(mappedBy = "chatRooms", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserChatRoom> userChatRoomList = new ArrayList<>();

    @OneToMany(mappedBy = "chatRooms", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Chat> chatList = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Review> reviewList = new ArrayList<>();

    public void updateActivated(boolean bool) {
        this.activated = bool;
    }

    public void updateTitle(String modifiedTitle) {
        this.chatTitle = modifiedTitle;
    }
}
