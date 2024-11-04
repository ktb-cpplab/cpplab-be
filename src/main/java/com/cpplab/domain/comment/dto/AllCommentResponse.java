package com.cpplab.domain.comment.dto;

import com.cpplab.domain.comment.entity.CommentEntity;
import com.cpplab.global.common.enums.Rank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AllCommentResponse(
    Long commentId, // 댓글 id
    Long userId, // 유저 id
//    Long postId, // 게시물 id
    String nickName, // 닉네임
    Rank rank, // 직위
    String content, // 댓글내용
    String profileImage, // 프로필
    LocalDateTime modifiedAt // 마지막 수정일시
) {
    public static AllCommentResponse from(CommentEntity comment) {
        return AllCommentResponse.builder()
                .commentId(comment.getCommentId())  // 댓글 ID
                .userId(comment.getUser().getUserId()) // 유저 ID
//                .postId(comment.getPost().getPostId()) // 게시물 ID
                .nickName(comment.getNickName())           // 닉네임
                .rank(comment.getRank())               // 직위
                .content(comment.getContent())         // 댓글 내용
                .profileImage(comment.getUser().getProfileImage()) // 프로필
                .modifiedAt(comment.getModifiedAt()) // 마지막 수정일시
                .build();
    }
}
