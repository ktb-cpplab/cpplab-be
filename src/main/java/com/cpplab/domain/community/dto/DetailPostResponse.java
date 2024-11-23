package com.cpplab.domain.community.dto;

import com.cpplab.domain.comment.dto.AllCommentResponse;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.domain.roadmap.dto.RoadmapResponse;
import com.cpplab.global.common.enums.Rank;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DetailPostResponse(
        Long postId,
        String title,
        String content,
        Long views,
        Long likes,
        Long commentCount,
        boolean isLike,
        Rank rank,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        PostUserResponse user,
        RoadmapResponse roadmap,
        List<AllCommentResponse> comments
//        List<CommentResponse> comments
) {
    @Builder
    public record PostUserResponse(
            Long userId,
            String nickName,
            String profileImage
    ) {}

    public record CommentResponse(
            Long commentId,
            Long userId,
            String nickName,
            Rank rank,
            String content,
            String profileImage,
            LocalDateTime modifiedAt
    ) {}

}


