package com.cpplab.domain.roadmap.dto;

import com.cpplab.domain.roadmap.entity.roadmap.StepEntity;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RoadmapAndLectureResponse(
        Long roadmapId,
        String title,
        String description,
        String difficultyLevel, // 프로젝트 수준
        String projectSummary, // 프로젝트 요약
        List<String> techStacks, // 주 기술스택
        List<StepEntity> steps,
        List<LectureResponse> lectures,
        LocalDateTime createdAt

) {
    public record LectureResponse(Long lectureId, String title, String url) {}
}
