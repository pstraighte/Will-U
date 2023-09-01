package com.beteam.willu.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beteam.willu.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findNotificationByReceiver_IdAndIsReadIsFalse(Long userId);

}
