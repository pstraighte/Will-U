package com.beteam.willu.blacklist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {

    Optional<Blacklist> findByIdAndId(Long id1, Long id2);

}
