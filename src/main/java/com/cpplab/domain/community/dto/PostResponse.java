package com.cpplab.domain.community.dto;

import com.cpplab.global.common.enums.Rank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponse(
    Long postId,
    String title,
    String content,
    Long views,
    Long likes,
    Long commentCount,
    boolean isLiked,
    Rank rank,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt,
    PostUserResponse user
) {
    @Builder
    public static record PostUserResponse(
            Long userId,
            String nickName,
            String profileImage,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {}
}
