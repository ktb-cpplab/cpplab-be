package com.cpplab.domain.roadmap.mapper;

import com.cpplab.domain.roadmap.dto.RoadmapAndLectureResponse;
import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import com.cpplab.domain.roadmap.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoadmapMapper {

    private final LectureRepository lectureRepository;

    public RoadmapAndLectureResponse toDto(RoadmapEntity roadmap) {
        List<RoadmapAndLectureResponse.LectureResponse> lectures = lectureRepository.findByRoadmapId(roadmap.getRoadmapId()).stream()
                .map(lecture -> new RoadmapAndLectureResponse.LectureResponse(
                        lecture.getLectureId(),
                        lecture.getTitle(),
                        lecture.getUrl()
                ))
                .collect(Collectors.toList());

        return RoadmapAndLectureResponse.builder()
                .roadmapId(roadmap.getRoadmapId())
                .title(roadmap.getTitle())
                .description(roadmap.getDescription())
                .difficultyLevel(roadmap.getDifficultyLevel())
                .projectSummary(roadmap.getProjectSummary())
                .techStacks(roadmap.getTechStacks())
                .steps(roadmap.getSteps())
                .createdAt(roadmap.getCreatedAt())
                .lectures(lectures)
                .build();
    }

    public List<RoadmapAndLectureResponse> toDtoList(List<RoadmapEntity> roadmaps) {
        return roadmaps.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
