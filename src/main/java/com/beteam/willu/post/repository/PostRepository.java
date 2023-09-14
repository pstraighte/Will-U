package com.beteam.willu.post.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


import com.beteam.willu.post.entity.Post;
import com.beteam.willu.user.entity.User;
import com.querydsl.core.types.Predicate;

public interface PostRepository extends JpaRepository<Post, Long>, QuerydslPredicateExecutor<Post> {
	Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

	Page<Post> findAllByOrderByIdDesc(Predicate predicate, Pageable pageable);

	List<Post> findAllByRecruitmentOrderByCreatedAtDesc(boolean b);

	List<Post> findByUserOrderByCreatedAtDesc(User user);
}
