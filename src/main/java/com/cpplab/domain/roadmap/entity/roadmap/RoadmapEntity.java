package com.cpplab.domain.roadmap.entity.roadmap;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class RoadmapEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roadmapId;

    private String title;
    private String description;
    private String difficultyLevel; // 프로젝트 수준
    private String projectSummary; // 프로젝트 요약

    @ElementCollection
    private List<String> techStacks; // 주 기술스택

    @ManyToOne // // 여러 개의 LikeEntity 하나의 UserEntity
    @JoinColumn(name = "userId")
    private UserEntity user;

    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StepEntity> steps = new ArrayList<>();
}
