package com.beteam.willu.repositoryTest;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.beteam.willu.notification.entity.Notification;
import com.beteam.willu.notification.entity.NotificationType;
import com.beteam.willu.notification.repository.NotificationRepository;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Notification Repository Test")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class NotificationTest {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NotificationRepository notificationRepository;

	User joiner;
	User writer;

	@BeforeEach
	void init() {
		joiner = User.builder()
			.username("testUser1")
			.password("password")
			.nickname("testUserNick1")
			.email("testUse1r@email.com")
			.build();
		userRepository.save(joiner);
		writer = User.builder()
			.username("testUser2")
			.password("password")
			.nickname("testUserNick2")
			.email("testUse2r@email.com")
			.build();
		userRepository.save(writer);

		Notification notification1 = Notification.builder()
			.id(1L)
			.receiver(writer)
			.publisher(joiner)
			.notificationType(NotificationType.JOIN_REQUEST)
			.content("참가신청했습니다.")
			.title("게시글 참가신청")
			.isRead(false)
			.build();
		notificationRepository.save(notification1);
		Notification notification2 = Notification.builder()
			.id(2L)
			.receiver(joiner)
			.publisher(writer)
			.notificationType(NotificationType.APPROVE_REQUEST)
			.content("참가 승인되었습니다.")
			.title("게시글 참가 승인")
			.isRead(true)
			.build();

		notificationRepository.save(notification2);
	}

	@DisplayName("Notification 테스트: 알림 Id로 조회")
	@Test
	@Order(1)
	void findById() {
		Assertions.assertEquals("게시글 참가신청", notificationRepository.findById(1L).get().getTitle());
		Assertions.assertEquals("게시글 참가 승인", notificationRepository.findById(2L).get().getTitle());
	}

	@DisplayName("Notification 테스트: 특정 수신자에게 온 읽지 않은 알림 조회")
	@Test
	@Order(2)
	void findNotificationByReceiver_IdAndIsReadIsFalse() {
		//given

		//when
		List<Notification> list = notificationRepository.findNotificationByReceiver_IdAndIsReadIsFalse(writer.getId());
		//then
		Assertions.assertEquals(1, list.size());
	}

}
