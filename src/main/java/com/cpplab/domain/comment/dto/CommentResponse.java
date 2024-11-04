package com.cpplab.domain.comment.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponse(
        Long commentId,
        String nickName,
        String content,
        String rank,
        Long userId,
        Long postId,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
}
