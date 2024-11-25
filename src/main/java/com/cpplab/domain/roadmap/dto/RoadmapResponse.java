package com.cpplab.domain.roadmap.dto;

import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import com.cpplab.domain.roadmap.entity.roadmap.StepEntity;
import com.cpplab.domain.roadmap.entity.roadmap.TaskEntity;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record RoadmapResponse(
        Long roadmapId,
        String title,
        String description,
        String difficultyLevel,
        String projectSummary,
        List<StepResponse> steps
) {
    public static RoadmapResponse from(RoadmapEntity roadmapEntity) {
        return RoadmapResponse.builder()
                .roadmapId(roadmapEntity.getRoadmapId())
                .title(roadmapEntity.getTitle())
                .description(roadmapEntity.getDescription())
                .difficultyLevel(roadmapEntity.getDifficultyLevel())
                .projectSummary(roadmapEntity.getProjectSummary())
                .steps(roadmapEntity.getSteps().stream()
                        .map(StepResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }

    @Builder
    public static record StepResponse(
            Long stepId,
            String stepTitle,
            List<TaskResponse> tasks
    ) {
        public static StepResponse from(StepEntity stepEntity) {
            return StepResponse.builder()
                    .stepId(stepEntity.getStepId())
                    .stepTitle(stepEntity.getStepTitle())
                    .tasks(stepEntity.getTasks().stream()
                            .map(TaskResponse::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Builder
    public static record TaskResponse(
            Long taskId,
            String stepTitle,
            boolean completed
    ) {
        public static TaskResponse from(TaskEntity taskEntity) {
            return TaskResponse.builder()
                    .taskId(taskEntity.getTaskId())
                    .stepTitle(taskEntity.getStepTitle())
                    .completed(taskEntity.isCompleted())
                    .build();
        }
    }
}
