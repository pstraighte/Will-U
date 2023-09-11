package com.beteam.willu.post.repository;

import com.beteam.willu.post.entity.Post;
import com.beteam.willu.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 제목으로 검색
    Page<Post> findByTitleContaining(String keyword, Pageable pageable);

    // 작성자의 username으로 검색
    Page<Post> findByUser_UsernameContaining(String keyword, Pageable pageable);

    // 내용으로 검색
    Page<Post> findByContentContaining(String keyword, Pageable pageable);

    // 제목으로 검색하고 모집 상태가 true인 게시글만 검색
    Page<Post> findByTitleContainingAndRecruitmentIsTrue(String keyword, Pageable pageable);

    // 작성자의 username으로 검색하고 모집 상태가 true인 게시글만 검색
    Page<Post> findByUser_UsernameContainingAndRecruitmentIsTrue(String keyword, Pageable pageable);

    // 내용으로 검색하고 모집 상태가 true인 게시글만 검색
    Page<Post> findByContentContainingAndRecruitmentIsTrue(String keyword, Pageable pageable);

    // 관심사으로 검색하고 모집 상태가 true인 게시글만 검색
//	Page<Post> findByCategoryContainingAndRecruitmentIsTrue(String keyword, Pageable pageable);

    //관심사로 검색
//	Page<Post> findByCategoryContaining(String keyword, Pageable pageable);

    List<Post> findAllByRecruitment(boolean b);

    List<Post> findByUser(User user);
}
