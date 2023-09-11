package com.beteam.willu.hashtag.repository;

import com.beteam.willu.hashtag.entity.BoardTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardTagMapRepository extends JpaRepository<BoardTagMap, Long> {

    List<BoardTagMap> findAllByTagId(Long id);

    // Tag_id 별로 개수를 내림차순으로 정렬하고 상위 5개 결과만 조회하는 쿼리
    @Query("SELECT b.tag.id, COUNT(b.tag.id) FROM BoardTagMap b GROUP BY b.tag.id ORDER BY COUNT(b.tag.id) DESC")
    List<Object[]> countTagsByTagIdDescLimit5();

    List<BoardTagMap> findAllByPostId(Long id);

    void deleteAllByPost_Id(Long id);

}
