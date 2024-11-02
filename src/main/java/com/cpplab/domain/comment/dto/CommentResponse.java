package com.cpplab.domain.comment.dto;

import com.cpplab.global.common.enums.Rank;
import lombok.Builder;

@Builder
public record CommentResponse(
    Long userId, // 닉네임 중복 시, 유저 식별자
    String nickName, // 닉네임
    String rank, // 직위
    String content, // 댓글내용
    Long postId // 게시물 아이디
) {
}
