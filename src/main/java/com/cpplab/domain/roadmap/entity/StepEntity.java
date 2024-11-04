package com.cpplab.domain.roadmap.entity;

import com.cpplab.domain.auth.entity.UserEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class StepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stepId;

    private String stepTitle;

    @ManyToOne // // 여러 개의 StepEntity 하나의 RoadmapEntity
    @JoinColumn(name = "roadmapId")
    private RoadmapEntity roadmap;
}