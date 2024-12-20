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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmap")
@Slf4j
public class RoadmapController {

    private final RestTemplate restTemplate;
    private final RoadmapService roadmapService;
    private final LectureRepository lectureRepository;
    private final RoadmapRepository roadmapRepository;

    // 로드맵 저장, url만 반환
    @PostMapping("")
    public ApiResponse<List<AiUrlResponse>> saveRoadmap(@AuthenticationPrincipal CustomOAuth2User customUser, @RequestBody RoadmapRequest roadmapRequest) {

        log.info("Executing on thread: {}", Thread.currentThread());
        log.info("Is Virtual Thread? {}", Thread.currentThread().isVirtual());

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

//    @PostMapping("/virtualthread")
//    public ApiResponse<String> virtualTest() {
//
////        HttpHeaders headers = new HttpHeaders();
////        headers.set("Content-Type", "application/json");
////        HttpEntity<AiUrlRequest> requestEntity = new HttpEntity<>(aiRequest, headers);
////
////        ResponseEntity<Map<String, String>[]> response = restTemplate.exchange(
////                aiUrl + "/ai/recommend",
////                HttpMethod.POST,
////                requestEntity,
////                (Class<Map<String, String>[]>) (Class<?>) Map[].class
////
//        log.info("Executing on thread: {}", Thread.currentThread());
//        // 요청 보내기
//        restTemplate.exchange(
//                "http://localhost:8081/ai/recommend", // 테스트 서버 URL
//                HttpMethod.POST,
//                null, // 요청 바디 필요 없음
//                String.class // 응답 타입
//                );
//
//        // 성공 메시지 반환
//        return ApiResponse.onSuccess("성공");
//    }

    @PostMapping("/syncgenproject")
    public ApiResponse<String> virtualTest1() {
        // 요청 바디 데이터 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("rank", "NOT_EXIST");
        requestBody.put("mainStack", Arrays.asList("Python", "R", "MongoDB", "FastAPI"));
        requestBody.put("hopeCompany", Arrays.asList("대기업"));
        requestBody.put("hopeJob", "AI Engineer");

        // 활동 데이터 추가
        List<Map<String, Object>> activities = new ArrayList<>();
        activities.add(Map.of(
                "title", "Google Machine Learning Bootcamp",
                "description", "Participated in Google's Machine Learning Bootcamp",
                "startDate", "NOT_PROVIDED",
                "endDate", "NOT_PROVIDED"
        ));
        activities.add(Map.of(
                "title", "Kakao Tech Bootcamp",
                "description", "Participated in Kakao's Tech Bootcamp",
                "startDate", "NOT_PROVIDED",
                "endDate", "NOT_PROVIDED"
        ));
        requestBody.put("activities", activities);

        // 자격증 데이터 추가
        List<Map<String, Object>> certificates = new ArrayList<>();
        certificates.add(Map.of("certificateName", "SQLD (SQL Developer)", "date", "NOT_PROVIDED"));
        certificates.add(Map.of("certificateName", "ADsP (Data Analysis Semi-Professional)", "date", "NOT_PROVIDED"));
        requestBody.put("certificates", certificates);

        // 교육 데이터 추가
        List<Map<String, Object>> educations = new ArrayList<>();
        educations.add(Map.of(
                "university", "건국대학교(서울)",
                "department", "응용통계학과",
                "gpa", 4.03,
                "gpaMax", 4.5
        ));
        requestBody.put("educations", educations);

        // 프로젝트 데이터 추가
        List<Map<String, Object>> projects = new ArrayList<>();
        projects.add(Map.of(
                "title", "Data Analysis and Machine Learning Projects",
                "description", "Worked on multiple data analysis and structured data machine learning projects.",
                "stacks", Arrays.asList("Python", "R", "Machine Learning", "Data Analysis")
        ));
        requestBody.put("projects", projects);

        // HttpEntity에 요청 바디와 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info("Executing on thread: {}", Thread.currentThread());

        // 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                "https://be.cpplab.store/ai/test/syncgenproject" , // 테스트 서버 URL
                HttpMethod.POST,
                requestEntity, // 요청 바디 포함
                String.class // 응답 타입
        );

        log.info("Response: {}", response.getBody());

        // 성공 메시지 반환
        return ApiResponse.onSuccess("성공");
    }

    @PostMapping("/asyncgenproject")
    public ApiResponse<String> virtualTest2() {
        // 요청 바디 데이터 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("rank", "NOT_EXIST");
        requestBody.put("mainStack", Arrays.asList("Python", "R", "MongoDB", "FastAPI"));
        requestBody.put("hopeCompany", Arrays.asList("대기업"));
        requestBody.put("hopeJob", "AI Engineer");

        // 활동 데이터 추가
        List<Map<String, Object>> activities = new ArrayList<>();
        activities.add(Map.of(
                "title", "Google Machine Learning Bootcamp",
                "description", "Participated in Google's Machine Learning Bootcamp",
                "startDate", "NOT_PROVIDED",
                "endDate", "NOT_PROVIDED"
        ));
        activities.add(Map.of(
                "title", "Kakao Tech Bootcamp",
                "description", "Participated in Kakao's Tech Bootcamp",
                "startDate", "NOT_PROVIDED",
                "endDate", "NOT_PROVIDED"
        ));
        requestBody.put("activities", activities);

        // 자격증 데이터 추가
        List<Map<String, Object>> certificates = new ArrayList<>();
        certificates.add(Map.of("certificateName", "SQLD (SQL Developer)", "date", "NOT_PROVIDED"));
        certificates.add(Map.of("certificateName", "ADsP (Data Analysis Semi-Professional)", "date", "NOT_PROVIDED"));
        requestBody.put("certificates", certificates);

        // 교육 데이터 추가
        List<Map<String, Object>> educations = new ArrayList<>();
        educations.add(Map.of(
                "university", "건국대학교(서울)",
                "department", "응용통계학과",
                "gpa", 4.03,
                "gpaMax", 4.5
        ));
        requestBody.put("educations", educations);

        // 프로젝트 데이터 추가
        List<Map<String, Object>> projects = new ArrayList<>();
        projects.add(Map.of(
                "title", "Data Analysis and Machine Learning Projects",
                "description", "Worked on multiple data analysis and structured data machine learning projects.",
                "stacks", Arrays.asList("Python", "R", "Machine Learning", "Data Analysis")
        ));
        requestBody.put("projects", projects);

        // HttpEntity에 요청 바디와 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info("Executing on thread: {}", Thread.currentThread());

        // 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                "https://be.cpplab.store/ai/test/asyncgenproject" , // 테스트 서버 URL
                HttpMethod.POST,
                requestEntity, // 요청 바디 포함
                String.class // 응답 타입
        );

        log.info("Response: {}", response.getBody());

        // 성공 메시지 반환
        return ApiResponse.onSuccess("성공");
    }


    @GetMapping("/virtualthread")
    public String recommend() throws InterruptedException {
        log.info("Executing on thread: {}", Thread.currentThread());
//        System.out.println("테스트 날라왔다");
        Thread.sleep(1000); // 1초 대기
        return "recommend";
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
