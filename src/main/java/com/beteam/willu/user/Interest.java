package com.beteam.willu.user;

import com.beteam.willu.common.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "interests")
public class Interest extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id") //받는 사람
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id") //보내는 사람
    private User sender;

    public Interest(User receiver, User sender) {
        this.receiver = receiver;
        this.sender = sender;
    }

}
