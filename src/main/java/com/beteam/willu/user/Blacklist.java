package com.beteam.willu.user;

import com.beteam.willu.common.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "blacklist")
public class Blacklist extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reciver_id") //받는 사람
    private Long receiver;

    @Column(name = "sender_id") //보내는 사람
    private Long sender;

    public Blacklist(Long receiver, Long sender) {
        this.receiver = receiver;
        this.sender = sender;
    }
}
