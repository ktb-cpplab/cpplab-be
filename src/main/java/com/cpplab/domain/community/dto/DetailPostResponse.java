package com.cpplab.domain.community.dto;

import com.cpplab.domain.comment.dto.CommentResponse;
import com.cpplab.domain.community.entity.PostEntity;

import java.util.List;

public record DetailPostResponse(
    PostEntity postEntity,
    List<CommentResponse> comments
) {
}
