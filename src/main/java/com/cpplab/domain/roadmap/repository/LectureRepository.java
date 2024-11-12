package com.cpplab.domain.roadmap.repository;

import com.cpplab.domain.roadmap.entity.LectureEntity;
import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<LectureEntity, Long> {

    @Query("SELECT l FROM LectureEntity l WHERE l.roadmap.roadmapId = :roadmapId")
    List<LectureEntity> findByRoadmapId(@Param("roadmapId") Long roadmapId);

    List<LectureEntity> findByRoadmap(RoadmapEntity roadmap);

}
