package com.beteam.willu.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beteam.willu.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findAllByOrderByCreatedAtDesc();
}
