package com.cpplab.domain.roadmap.dto;

import java.util.List;

public record StepRequest(
        Long stepId,
        String stepTitle,
        List<String> tasks
) {
}
