package com.beteam.willu.interest;

import com.beteam.willu.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {
    Optional<Interest> findByid1Andid2(Long id1, Long id2);
}
