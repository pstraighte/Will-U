package com.beteam.willu.stomp.entity;

import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "UserChatRooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userChatRooms_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name= "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name= "chatRooms_id")
    private ChatRoom chatRooms;

    //권한
    @Column(name = "role")
    private String role;

}
