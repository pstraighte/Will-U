package com.beteam.willu.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beteam.willu.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findAllByReceiverId(Long id);

	Review findBySenderIdAndChatRoomIdAndReceiverId(Long user, Long chatRoomId, Long userId);

	List<Review> findAllBySenderIdAndChatRoomId(Long user, Long chatRoomId);

	List<Review> findByReceiverId(Long id);

	List<Review> findBySenderId(Long id);
}
