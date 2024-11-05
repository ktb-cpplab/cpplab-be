package com.cpplab.domain.roadmap.entity.roadmap;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class StepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stepId;

    private String stepTitle;

    @ManyToOne // // 여러 개의 StepEntity 하나의 RoadmapEntity
    @JoinColumn(name = "roadmapId")
    @JsonBackReference // 직렬화 방어
    private RoadmapEntity roadmap;

//    @OneToMany(mappedBy = "step", cascade = CascadeType.PERSIST)
    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskEntity> tasks = new ArrayList<>();
}
