package com.cpplab.domain.community.dto;

import lombok.Builder;

public record PostRequest(
        String title, // 제목
        String content, // 내용
        Long views, // 조회수
        Long likes // 조회수

) {
    @Builder
    public record PostPutDto(
            String title, // 제목
            String content, // 내용
            Long roadmapId // 선택적 필드로 사용
    ){}
}
