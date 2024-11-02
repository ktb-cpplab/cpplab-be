package com.cpplab.domain.comment.entity;

import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.global.common.BaseEntity;
import com.cpplab.global.common.enums.Rank;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String name; // 소셜닉네임
    private String content; // 댓글내용

    @Enumerated(EnumType.STRING)
    @Column(name = "userRank") // rank예약어가 있음
    private Rank rank; // 랭크

    @ManyToOne
    @JoinColumn(name = "postId")
    private PostEntity post;
}
