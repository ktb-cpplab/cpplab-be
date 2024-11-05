package com.cpplab.domain.roadmap.entity;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LectureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    private String title; // 제목
    private String url; // url

    @ManyToOne // 여러 개의 PostEntity가 하나의 LectureEntity
    @JoinColumn(name = "roadmapId")
    private RoadmapEntity roadmap;
}
