package com.cpplab.domain.community.repository;

import com.cpplab.domain.community.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    // userId와 postId를 기준으로 LikeEntity 찾기
    Optional<LikeEntity> findByUserUserNameAndPostPostId(String userName, Long postId);
}
