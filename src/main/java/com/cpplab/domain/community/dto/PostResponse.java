package com.cpplab.domain.community.dto;

import lombok.Builder;

@Builder
public record PostResponse(
    String title,
    String content,
    Long views,
    Long likes
) {
}
