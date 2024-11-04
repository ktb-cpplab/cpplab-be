package com.cpplab.domain.roadmap.entity;

import jakarta.persistence.*;

@Entity
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private String stepTitle; // 업무제목
    private boolean isCompleted; // 완료 여부

    @ManyToOne // // 여러 개의 StepEntity 하나의 RoadmapEntity
    @JoinColumn(name = "roadmapId")
    private StepEntity step;

}
