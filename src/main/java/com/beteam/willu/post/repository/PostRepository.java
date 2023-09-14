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
    Page<Post> findByTitleContainingOrderByCreatedAtDesc(String keyword, Pageable pageable);

    // 작성자의 username으로 검색
    Page<Post> findByUser_NicknameContainingOrderByCreatedAtDesc(String keyword, Pageable pageable);

    // 내용으로 검색
    Page<Post> findByContentContainingOrderByCreatedAtDesc(String keyword, Pageable pageable);

    // 제목으로 검색하고 모집 상태가 true인 게시글만 검색
    Page<Post> findByTitleContainingAndRecruitmentIsTrueOrderByCreatedAtDesc(String keyword, Pageable pageable);

    // 작성자의 username으로 검색하고 모집 상태가 true인 게시글만 검색
    Page<Post> findByUser_NicknameContainingAndRecruitmentIsTrueOrderByCreatedAtDesc(String keyword, Pageable pageable);

    // 내용으로 검색하고 모집 상태가 true인 게시글만 검색
    Page<Post> findByContentContainingAndRecruitmentIsTrueOrderByCreatedAtDesc(String keyword, Pageable pageable);

    List<Post> findAllByRecruitmentOrderByCreatedAtDesc(boolean b);

    List<Post> findByUserOrderByCreatedAtDesc(User user);
}
