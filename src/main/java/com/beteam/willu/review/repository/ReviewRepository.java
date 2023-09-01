package com.beteam.willu.review.repository;

import com.beteam.willu.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByReceiverId(Long id);

    Review findBySenderIdAndChatRoomIdAndReceiverId(Long user, Long chatRoomId, Long userId);

    List<Review> findAllBySenderIdAndChatRoomId(Long user, Long chatRoomId);

}
