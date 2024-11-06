package com.cpplab.domain.roadmap.dto;

import java.util.List;

public record RoadmapRequest(
        String title,
        String description,
        List<String> techStacks, // 주 기술 스택 리스트
        String difficultyLevel, // 프로젝트 수준
        String projectSummary, // 프로젝트 요약
        List<StepRequest> steps // 스텝 리스트
) {
}
