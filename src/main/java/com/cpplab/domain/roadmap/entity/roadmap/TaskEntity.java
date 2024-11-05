package com.cpplab.domain.roadmap.entity.roadmap;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private String stepTitle; // 업무제목
    private boolean isCompleted = false; // 완료 여부, 기본 false

    @ManyToOne // // 여러 개의 StepEntity 하나의 RoadmapEntity
    @JoinColumn(name = "stepId")
    @JsonBackReference // 직렬화 방어
    private StepEntity step;
}
