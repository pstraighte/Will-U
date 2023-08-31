package com.beteam.willu.notification.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.beteam.willu.common.Timestamped;
import com.beteam.willu.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends Timestamped {
	/**
	 * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Long id;

	private String title;

	private String content;

	@Column(nullable = false)
	private Boolean isRead;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType notificationType;

	//TODO :STUDY OnDelete 와 연관관계에서 cascade 사용하는 것의 차이?
	@ManyToOne
	@JoinColumn(name = "receiver_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User receiver;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publisher_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User publisher;

	@Transient
	private Long postId;
	/**
	 * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
	 */

	/**
	 * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
	 */

	/**
	 * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
	 */

	/**
	 * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
	 */
	public void updateIsRead() {
		this.isRead = !this.isRead;
	}

}
