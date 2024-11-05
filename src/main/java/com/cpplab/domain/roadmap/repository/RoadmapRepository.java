package com.cpplab.domain.roadmap.repository;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapRepository extends JpaRepository<RoadmapEntity, Long> {
    List<RoadmapEntity> findByUser(UserEntity user); // 특정 유저의 로드맵을 조회하는 메서드
}
