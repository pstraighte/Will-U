package com.beteam.willu.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beteam.willu.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
