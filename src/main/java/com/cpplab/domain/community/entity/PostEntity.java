package com.cpplab.domain.community.entity;

import com.cpplab.domain.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PostEntity {

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
}
