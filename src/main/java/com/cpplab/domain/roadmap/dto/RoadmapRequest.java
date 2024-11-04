package com.cpplab.domain.roadmap.dto;

import java.util.List;

public record RoadmapRequest(
        String title,
        String description,
        List<String> techStacks, // 주 기술 스택 리스트
        List<StepRequest> steps // 스텝 리스트
) {
}
