package com.beteam.willu.user.review;

import com.beteam.willu.user.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByReceiverId(Long id);

}
