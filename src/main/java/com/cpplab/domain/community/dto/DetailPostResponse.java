package com.cpplab.domain.community.dto;

import com.cpplab.domain.comment.dto.AllCommentResponse;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.global.common.enums.Rank;

import java.util.List;

public record DetailPostResponse(
    PostEntity postEntity,
    Rank rank,
    List<AllCommentResponse> comments
) {
}


