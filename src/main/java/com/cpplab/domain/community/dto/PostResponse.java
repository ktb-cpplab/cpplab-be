package com.cpplab.domain.community.dto;

import lombok.Builder;

@Builder
public record PostResponse(
    Long postId,
    String title,
    String content,
    Long views,
    Long likes,
    PostUserResponse user

) {
    @Builder
    public static record PostUserResponse(
            Long userId,
            String nickName,
            String profileImage,
            String createdAt,
            String modifiedAt
    ) {}
}
