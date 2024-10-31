package com.cpplab.domain.community.entity;

import com.cpplab.domain.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "postId"})
}) // 유저1은 게시물당 좋아요 행이 1개씩만 생성된다.(따라서 유일하다)
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne // // 여러 개의 LikeEntity 하나의 UserEntity
    @JoinColumn(name = "userId")
    private UserEntity user;

    @ManyToOne // // 여러 개의 LikeEntity 하나의 PostEntity
    @JoinColumn(name = "postId")
    private PostEntity post;
}
