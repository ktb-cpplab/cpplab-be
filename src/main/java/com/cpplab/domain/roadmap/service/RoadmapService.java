package com.cpplab.domain.roadmap.service;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.auth.repository.UserRepository;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.domain.community.repository.PostRepository;
import com.cpplab.domain.mypage.entity.PortfolioEntity;
import com.cpplab.domain.mypage.repository.PortfolioRepository;
import com.cpplab.domain.roadmap.dto.*;
import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import com.cpplab.domain.roadmap.entity.roadmap.StepEntity;
import com.cpplab.domain.roadmap.entity.roadmap.TaskEntity;
import com.cpplab.domain.roadmap.repository.RoadmapRepository;
import com.cpplab.domain.roadmap.repository.TaskRepository;
import com.cpplab.global.common.code.status.ErrorStatus;
import com.cpplab.global.common.enums.Rank;
import com.cpplab.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    @Value("${ai.url}")
    private String aiUrl;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final RoadmapRepository roadmapRepository;
    private final TaskRepository taskRepository;
    private final PostRepository postRepository;
    private final PortfolioRepository portfolioRepository;

    public RoadmapEntity saveRoadmap(Long userId, RoadmapRequest roadmapRequest) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_USER));

        RoadmapEntity roadmap = new RoadmapEntity();
        roadmap.setTitle(roadmapRequest.title());
        roadmap.setDescription(roadmapRequest.description());
        roadmap.setTechStacks(roadmapRequest.techStacks());
        roadmap.setUser(user);

        for (StepRequest stepRequest : roadmapRequest.steps()) {
            StepEntity step = new StepEntity();
            step.setStepTitle(stepRequest.stepTitle());
            step.setRoadmap(roadmap);
            roadmap.getSteps().add(step);

            for (String taskTitle : stepRequest.tasks()) { // 문자열로 받아옴
                TaskEntity task = new TaskEntity();
                task.setStepTitle(taskTitle);
                task.setCompleted(false); // 초기화 상태
                task.setStep(step);
                step.getTasks().add(task);
            }
        }
        return roadmapRepository.save(roadmap);
    }

    public List<RoadmapEntity> readAllRoadmap(Long userId) {
        // 유저가 존재하는지 검증
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_USER));

        // 유저의 로드맵을 조회
        List<RoadmapEntity> roadmaps = roadmapRepository.findByUser(user);

        if (roadmaps.isEmpty()) {
            throw new GeneralException(ErrorStatus._NOT_FOUND_ROADMAP); // 로드맵이 없을 때 예외 처리
        }
        return roadmaps;
    }

    public RoadmapEntity readRoadmap(Long userId, Long roadmapId) {
        RoadmapEntity roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_ROADMAP));

        if (roadmap.getUser().getUserId() != userId) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED_ACCESS_ROADMAP);
        }
        return roadmap;
    }

    @Transactional
    public void deleteRoadmap(Long userId, Long roadmapId) {
        RoadmapEntity roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_ROADMAP));

        if (roadmap.getUser().getUserId() != userId) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED_DELETE_ROADMAP);
        }

        // PostEntity에서 roadmap 참조를 null로 설정
        List<PostEntity> postsWithRoadmap = postRepository.findByRoadmap(roadmap);
        for (PostEntity post : postsWithRoadmap) {
            post.setRoadmap(null); // roadmap 참조를 해제
            postRepository.save(post); // 변경 사항 저장
        }

        roadmapRepository.delete(roadmap);
    }

    @Transactional
    public Boolean stepCheck(Long userId, Long roadmapId ,Long taskId) {
        // taskId로 TaskEntity 조회
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_TASK));

        // 테스크의 유저인지 검사, 도메인 로직이 서비스 로직에 수정하기 getowner
        if (task.getStep().getRoadmap().getUser().getUserId() != userId) { // equal 로 비교, 멘토님, task라는 객체를 정의해서 task안에서 메소드로 해석.
            throw new GeneralException(ErrorStatus._UNAUTHORIZED_ACCESS_TASK);
        }
        task.setCompleted(!task.isCompleted()); // 상태바꾸기
        return task.isCompleted();
    }

    public List<AiUrlResponse> getRecommendations(Long userId, RoadmapRequest roadmapRequest) {
        PortfolioEntity portfolioEntity = portfolioRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_PORTFOLIO));

        // 2. AiRecommendationRequest 객체 생성 (필요한 필드들을 추출)
        AiUrlRequest aiRequest = new AiUrlRequest(
                portfolioEntity.getHopeJob(),               // hopeJob 필드
                roadmapRequest.techStacks(),             // techStacks 필드
                roadmapRequest.difficultyLevel(),       // difficultyLevel 필드
                roadmapRequest.title(),                  // projectTitle 필드
                roadmapRequest.projectSummary()             // projectDescription 필드
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<AiUrlRequest> requestEntity = new HttpEntity<>(aiRequest, headers);

        ResponseEntity<Map<String, Object>[]> response = restTemplate.exchange(
                aiUrl + "/ai/recommend",
                HttpMethod.POST,
                requestEntity,
                (Class<Map<String, Object>[]>) (Class<?>) Map[].class
        );

        // Map response to AiUrlResponse objects
        return Arrays.stream(response.getBody())
                .map(data -> {
                    AiUrlResponse aiUrlResponse = new AiUrlResponse();
                    aiUrlResponse.setTitle((String) data.get("title"));
                    aiUrlResponse.setUrl((String) data.get("url"));
                    return aiUrlResponse; // 인스턴스 aiUrlResponse를 반환
                })
                .collect(Collectors.toList());

    }

//    public List<AiUrlResponse> getRecommendations(RoadmapRequest roadmapRequest) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "application/json");
//
//        HttpEntity<RoadmapRequest> requestEntity = new HttpEntity<>(roadmapRequest, headers);
//
//        ResponseEntity<Map<String, Object>[]> response = restTemplate.exchange(
//                aiAPiUrl + "/ai/recommend",
//                HttpMethod.POST,
//                requestEntity,
//                Map[].class
//        );
////        return List.of(response.getBody());
//
//        // Map response to CustomAiUrlResponse objects
//        return Arrays.stream(response.getBody())
//                .map(data -> {
//                    AiUrlResponse aiUrlResponse = new AiUrlResponse();
//                    aiUrlResponse.setHopeJob((String) data.get("hope_job"));
//                    aiUrlResponse.setProjectLevel((String) data.get("project_level"));
//                    aiUrlResponse.setProjectStack((List<String>) data.get("project_stack"));
//                    aiUrlResponse.setProjectTitle((String) data.get("project_title"));
//                    aiUrlResponse.setProjectDescription((String) data.get("project_description"));
//                    return customResponse;
//                })
//                .collect(Collectors.toList());
//    }


}
