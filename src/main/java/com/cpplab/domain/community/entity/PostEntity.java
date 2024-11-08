package com.cpplab.domain.community.entity;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import com.cpplab.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title; // 제목
    private String content; // 내용
    private Long views; // 조회수
    private Long likes; // 좋아요

    @ManyToOne // 여러 개의 PostEntity가 하나의 UserEntity
    @JoinColumn(name = "userId")
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "roadmapId", nullable = true) // null 허용
    private RoadmapEntity roadmap;
}
