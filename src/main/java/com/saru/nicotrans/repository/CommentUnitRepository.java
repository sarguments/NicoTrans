package com.saru.nicotrans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentUnitRepository extends JpaRepository<CommentUnit, Long> {
    CommentUnit findByReferer(String referer);
}
