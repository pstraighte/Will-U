package com.beteam.willu.hashtag.repository;

import com.beteam.willu.hashtag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<Tag, Long> {
    Tag findByContent(String content);

    Boolean existsByContent(String content);

    Optional<Tag> findTagByContent(String content);

}
