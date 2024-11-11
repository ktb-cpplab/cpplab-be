package com.cpplab.domain.roadmap.controller;

import com.cpplab.domain.auth.dto.CustomOAuth2User;
import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.roadmap.dto.*;
import com.cpplab.domain.roadmap.entity.LectureEntity;
import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import com.cpplab.domain.roadmap.repository.LectureRepository;
import com.cpplab.domain.roadmap.repository.RoadmapRepository;
import com.cpplab.domain.roadmap.service.RoadmapService;
import com.cpplab.global.common.ApiResponse;
import com.cpplab.global.common.code.status.ErrorStatus;
import com.cpplab.global.common.exception.GeneralException;
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
    private final RoadmapRepository roadmapRepository;

    // 로드맵 저장, url만 반환
    @PostMapping("")
    public ApiResponse<List<AiUrlResponse>> saveRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser, @RequestBody RoadmapRequest roadmapRequest) {
        RoadmapEntity savedRoadmap = roadmapService.saveRoadmap(customUser.getUserId(), roadmapRequest);

        // 2. AI 추천 API에 요청 보내기
        List<AiUrlResponse> aiRecommendations = roadmapService.getRecommendations(customUser.getUserId(), roadmapRequest);

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

        return ApiResponse.onSuccess(aiRecommendations);
    }

    // 로드맵 전체 조회
    @GetMapping("")
    public ApiResponse<List<RoadmapAndLectureResponse>> readAllRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser) {
        return ApiResponse.onSuccess(roadmapService.readAllRoadmap(customUser.getUserId()));
    }

    // 로드맵 조회
    @GetMapping("/{roadmapId}")
    public ApiResponse<RoadmapAndLectureResponse> readRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser, @PathVariable("roadmapId") Long roadmapId) {
        return ApiResponse.onSuccess(roadmapService.readRoadmap(customUser.getUserId(), roadmapId));
    }

    // 로드맵 삭제
    @DeleteMapping("/{roadmapId}")
    public ApiResponse<String> deleteRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser, @PathVariable("roadmapId") Long roadmapId) {
        roadmapService.deleteRoadmap(customUser.getUserId(), roadmapId);
        return  ApiResponse.onSuccess("로드맵이 성공적으로 삭제되었습니다");
    }

    // 로드맵 스탭에서 체크
    @PatchMapping("/{roadmapId}/task/{taskId}")
    public ApiResponse<Boolean> stepCheck(@AuthenticationPrincipal CustomOAuth2User customUser,
                                          @PathVariable("roadmapId") Long roadmapId,
                                          @PathVariable("taskId") Long taskId) {
        Boolean toggleStatus = roadmapService.stepCheck(customUser.getUserId(), roadmapId, taskId);
        return ApiResponse.onSuccess(toggleStatus);
    }


    // AI 안될 때, 로드맵 임시 저장.
    @PostMapping("/temp")
    public ApiResponse<String> tempSaveRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser, @RequestBody RoadmapRequest roadmapRequest) {
        RoadmapEntity savedRoadmap = roadmapService.saveRoadmap(customUser.getUserId(), roadmapRequest);
        return ApiResponse.onSuccess("로드맵 임시 저장 하기 성공");
    }

    // AI 안될 때, 로드맵 이름으로 url 삽입
    @PostMapping("{roadmapId}/temp/url")
    public ApiResponse<String> tempSaveRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser, @RequestBody AiUrlResponse aiUrlResponse,
                                               @PathVariable("roadmapId") Long roadmapId) {

        RoadmapEntity roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_ROADMAP));

        LectureEntity lectureEntity = new LectureEntity();
        lectureEntity.setUrl(aiUrlResponse.getUrl());
        lectureEntity.setTitle(aiUrlResponse.getTitle());
        lectureEntity.setRoadmap(roadmap);
        lectureRepository.save(lectureEntity);

        return ApiResponse.onSuccess("url 임시 저장 하기 성공");
    }

}
