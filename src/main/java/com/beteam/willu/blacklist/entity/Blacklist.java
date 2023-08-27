package com.beteam.willu.blacklist.entity;

import com.beteam.willu.common.Timestamped;
import com.beteam.willu.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id") //받는 사람
	//    @Column(name = "receiver_id")
	private User receiver;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id") //보내는 사람
	//    @Column(name = "sender_id")
	private User sender;

	public Blacklist(User receiver, User sender) {
		this.receiver = receiver;
		this.sender = sender;
	}
}
