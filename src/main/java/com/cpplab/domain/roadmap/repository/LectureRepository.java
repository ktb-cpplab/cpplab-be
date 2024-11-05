package com.cpplab.domain.roadmap.repository;

import com.cpplab.domain.roadmap.entity.LectureEntity;
import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepository extends JpaRepository<LectureEntity, Long> {
}
