package com.cpplab.domain.roadmap.controller;

import com.cpplab.domain.auth.dto.CustomOAuth2User;
import com.cpplab.domain.roadmap.dto.AiUrlResponse;
import com.cpplab.domain.roadmap.dto.CustomAiUrlResponse;
import com.cpplab.domain.roadmap.dto.RoadmapRequest;
import com.cpplab.domain.roadmap.entity.LectureEntity;
import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import com.cpplab.domain.roadmap.repository.LectureRepository;
import com.cpplab.domain.roadmap.service.RoadmapService;
import com.cpplab.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmap")
public class RoadmapController {

    private final RoadmapService roadmapService;
    private final LectureRepository lectureRepository;

    // 로드맵 저장, url만 반환
    @PostMapping("")
    public ApiResponse<List<LectureEntity>> saveRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser, @RequestBody RoadmapRequest roadmapRequest) {
        RoadmapEntity savedRoadmap = roadmapService.saveRoadmap(customUser.getUserId(), roadmapRequest);

        // 2. AI 추천 API에 요청 보내기
        List<AiUrlResponse> aiRecommendations = roadmapService.getRecommendations(roadmapRequest);
//        List<AiUrlResponse> aiRecommendations = roadmapService.getRecommendations(roadmapRequest);

        // 3. AI 추천 결과를 LectureEntity로 변환하여 DB에 저장
        List<LectureEntity> lectures = aiRecommendations.stream()
                .map(recommendation -> {
                    LectureEntity lecture = new LectureEntity();
                    lecture.setTitle(recommendation.getTitle());
                    lecture.setUrl(recommendation.getUrl());
                    lecture.setRoadmap(savedRoadmap); // 연관된 Roadmap 설정
                    return lectureRepository.save(lecture); // LectureEntity 저장
                })
                .collect(Collectors.toList());

        return ApiResponse.onSuccess(lectures);
    }

    // 로드맵 전체 조회
    @GetMapping("")
    public ApiResponse<List<RoadmapEntity>> readAllRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser) {
        return ApiResponse.onSuccess(roadmapService.readAllRoadmap(customUser.getUserId()));
    }

    // 로드맵 조회
    @GetMapping("/{roadmapId}")
    public ApiResponse<RoadmapEntity> readRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser, @PathVariable("roadmapId") Long roadmapId) {
        return ApiResponse.onSuccess(roadmapService.readRoadmap(customUser.getUserId(), roadmapId));
    }

    // 로드맵 삭제
    @DeleteMapping("/{roadmapId}")
    public ApiResponse<String> deleteRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser, @PathVariable("roadmapId") Long roadmapId) {
        roadmapService.deleteRoadmap(customUser.getUserId(), roadmapId);
        return  ApiResponse.onSuccess("로드맵이 성공적으로 삭제되었습니다");
    }

    // 로드맵 스탭에서 체크
    @PatchMapping("/{taskId}")
    public ApiResponse<Boolean> stepCheck(@AuthenticationPrincipal CustomOAuth2User customUser, @PathVariable("taskId") Long taskId) {
        Boolean toggleStatus = roadmapService.stepCheck(customUser.getUserId(), taskId);
        return ApiResponse.onSuccess(toggleStatus);
    }
}
