package com.cpplab.domain.roadmap.entity;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.global.common.BaseEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class RoadmapEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roadmapId;

    private String title;
    private String description;

    @ElementCollection
    private List<String> mainStacks; // 주 기술스택

    @ManyToOne // // 여러 개의 LikeEntity 하나의 UserEntity
    @JoinColumn(name = "userId")
    private UserEntity user;
}
