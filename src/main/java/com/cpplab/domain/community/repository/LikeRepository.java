package com.cpplab.domain.community.repository;

import com.cpplab.domain.community.entity.LikeEntity;
import com.cpplab.domain.community.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    // userId와 postId를 기준으로 LikeEntity 찾기
    Optional<LikeEntity> findByUserUserIdAndPostPostId(Long userId, Long postId);

    void deleteByPost(PostEntity post);

}
