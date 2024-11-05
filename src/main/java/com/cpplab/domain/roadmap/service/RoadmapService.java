package com.cpplab.domain.roadmap.service;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.auth.repository.UserRepository;
import com.cpplab.domain.roadmap.dto.AiUrlResponse;
import com.cpplab.domain.roadmap.dto.RoadmapRequest;
import com.cpplab.domain.roadmap.dto.StepRequest;
import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import com.cpplab.domain.roadmap.entity.roadmap.StepEntity;
import com.cpplab.domain.roadmap.entity.roadmap.TaskEntity;
import com.cpplab.domain.roadmap.repository.RoadmapRepository;
import com.cpplab.domain.roadmap.repository.TaskRepository;
import com.cpplab.global.common.code.status.ErrorStatus;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    @Value("${ai.api.url}")
    private String aiAPiUrl;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final RoadmapRepository roadmapRepository;
    private final TaskRepository taskRepository;

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

    public List<RoadmapEntity> readRoadmap(Long userId) {
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

    @Transactional
    public void deleteRoadmap(Long userId, Long roadmapId) {
        RoadmapEntity roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_ROADMAP));

        if (roadmap.getUser().getUserId() != userId) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED_ACCESS_ROADMAP);
        }

        roadmapRepository.delete(roadmap);
    }

    @Transactional
    public Boolean stepCheck(Long userId, Long taskId) {
        // taskId로 TaskEntity 조회
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_TASK));

        // 테스크의 유저인지 검사
        if (task.getStep().getRoadmap().getUser().getUserId() != userId) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED_ACCESS_TASK);
        }
        task.setCompleted(!task.isCompleted()); // 상태바꾸기
        return task.isCompleted();
    }


    public List<AiUrlResponse> getRecommendations(RoadmapRequest roadmapRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<RoadmapRequest> requestEntity = new HttpEntity<>(roadmapRequest, headers);

        ResponseEntity<AiUrlResponse[]> response = restTemplate.exchange(
                aiAPiUrl + "/ai/recommend",
                HttpMethod.POST,
                requestEntity,
                AiUrlResponse[].class
        );
        return List.of(response.getBody());
    }


}
