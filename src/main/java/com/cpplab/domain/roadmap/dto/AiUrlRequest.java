package com.cpplab.domain.roadmap.dto;

import java.util.List;

public record AiUrlRequest(
        String hopeJob,  // 희망 직무
        List<String> techStacks,  // 주 기술 스택 리스트
        String difficultyLevel,  // 프로젝트 난이도
        String projectTitle,  // 프로젝트 제목
        String projectSummary  // 프로젝트 설명
) {
}
