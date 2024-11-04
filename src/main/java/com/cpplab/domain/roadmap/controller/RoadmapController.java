package com.cpplab.domain.roadmap.controller;

import com.cpplab.domain.auth.dto.CustomOAuth2User;
import com.cpplab.domain.mypage.service.MyService;
import com.cpplab.domain.roadmap.dto.RoadmapRequest;
import com.cpplab.domain.roadmap.entity.RoadmapEntity;
import com.cpplab.domain.roadmap.service.RoadmapService;
import com.cpplab.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmap")
public class RoadmapController {

    private final RoadmapService roadmapService;

//    @GetMapping("")
//    public String save(){
//        System.out.println("asdasd");
//        return "string";
//    }

    // 로드맵 저장
    @PostMapping("")
    public ApiResponse<RoadmapEntity> saveRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser, @RequestBody RoadmapRequest roadmapRequest) {
        return ApiResponse.onSuccess(roadmapService.saveRoadmap(customUser.getUserId(), roadmapRequest));
    }


    // 로드맵 조회
    @GetMapping("")
    public ApiResponse<List<RoadmapEntity>> readRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser) {
        return ApiResponse.onSuccess(roadmapService.readRoadmap(customUser.getUserId()));
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
