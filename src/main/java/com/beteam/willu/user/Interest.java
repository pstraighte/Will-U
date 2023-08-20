package com.beteam.willu.user;

import com.beteam.willu.common.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "interests")
public class Interest extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reciver_id") //받는 사람
    private Long receiver;

    @Column(name = "sender_id") //보내는 사람
    private Long sender;

    public Interest(Long receiver, Long sender) {
        this.receiver = receiver;
        this.sender = sender;
    }

}
