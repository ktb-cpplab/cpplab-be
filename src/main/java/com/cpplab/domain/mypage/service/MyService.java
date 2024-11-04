package com.cpplab.domain.mypage.service;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.auth.repository.UserRepository;
import com.cpplab.domain.mypage.dto.PortfolioRequest;
import com.cpplab.domain.mypage.dto.UserInfoRequest;
import com.cpplab.domain.mypage.dto.detail.*;
import com.cpplab.domain.mypage.entity.PortfolioEntity;
import com.cpplab.domain.mypage.entity.detail.*;
import com.cpplab.domain.mypage.repository.PortfolioRepository;
import com.cpplab.global.common.code.status.ErrorStatus;
import com.cpplab.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MyService {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

    public UserEntity getUserInfo(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_USER));
    }

    public UserEntity putUserInfo(Long userId, UserInfoRequest request) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_USER));

        userEntity.setNickName(request.nickName()); // 새로운 닉네임 업데이트
        userRepository.save(userEntity); // 변경사항 저장
        return userEntity;
    }

    public PortfolioEntity getPortfolio(Long userId){
        return portfolioRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_PORTFOLIO));
    }
    @Transactional
    public PortfolioEntity putPortfolio(Long userId, PortfolioRequest request) {
        // 유저 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_USER));

        // 포트폴리오 조회 또는 생성
        PortfolioEntity portfolio = portfolioRepository.findByUser(user)
                .orElse(new PortfolioEntity());

        // PortfolioEntity의 기본 필드 설정
        portfolio.setUser(user);
        portfolio.setRank(request.rank());
        portfolio.setMainStack(request.mainStack());
        portfolio.setHopeCompany(request.hopeCompany());
        portfolio.setHopeJob(request.hopeJob());

        // 활동 리스트 설정 - 기존 컬렉션을 clear하고 새 항목 추가
        if (portfolio.getActivities() != null) {
            portfolio.getActivities().clear();
        } else {
            portfolio.setActivities(new ArrayList<>());
        }
        for (ActivityRequest activityRequest : request.activities()) {
            ActivityEntity activity = new ActivityEntity(null, activityRequest.title(),
                    activityRequest.description(), activityRequest.startDate(),
                    activityRequest.endDate(), portfolio);
            portfolio.getActivities().add(activity);
        }

        // 자격증 리스트 설정
        if (portfolio.getCertificates() != null) {
            portfolio.getCertificates().clear();
        } else {
            portfolio.setCertificates(new ArrayList<>());
        }
        for (CertificateRequest certRequest : request.certificates()) {
            CertificateEntity certificate = new CertificateEntity(null, certRequest.certificateName(),
                    certRequest.date(), portfolio);
            portfolio.getCertificates().add(certificate);
        }

        // 회사 리스트 설정
        if (portfolio.getCompanies() != null) {
            portfolio.getCompanies().clear();
        } else {
            portfolio.setCompanies(new ArrayList<>());
        }
        for (CompanyRequest companyRequest : request.companies()) {
            CompanyEntity company = new CompanyEntity(null, companyRequest.company(),
                    companyRequest.job(), companyRequest.startDate(),
                    companyRequest.endDate(), portfolio);
            portfolio.getCompanies().add(company);
        }

        // 교육 리스트 설정
        if (portfolio.getEducations() != null) {
            portfolio.getEducations().clear();
        } else {
            portfolio.setEducations(new ArrayList<>());
        }
        for (EducationRequest eduRequest : request.educations()) {
            EducationEntity education = new EducationEntity(null, eduRequest.university(),
                    eduRequest.department(), eduRequest.gpa(),
                    eduRequest.gpaMax(), portfolio);
            portfolio.getEducations().add(education);
        }

        // 수상 리스트 설정
        if (portfolio.getPrizes() != null) {
            portfolio.getPrizes().clear();
        } else {
            portfolio.setPrizes(new ArrayList<>());
        }
        for (PrizeRequest prizeRequest : request.prizes()) {
            PrizeEntity prize = new PrizeEntity(null, prizeRequest.title(),
                    prizeRequest.description(), prizeRequest.date(), portfolio);
            portfolio.getPrizes().add(prize);
        }

        // 프로젝트 리스트 설정
        if (portfolio.getProjects() != null) {
            portfolio.getProjects().clear();
        } else {
            portfolio.setProjects(new ArrayList<>());
        }
        for (ProjectRequest projectRequest : request.projects()) {
            ProjectEntity project = new ProjectEntity(null, projectRequest.title(),
                    projectRequest.description(), projectRequest.stacks(), portfolio);
            portfolio.getProjects().add(project);
        }

        // 포트폴리오 저장 및 반환
        return portfolioRepository.save(portfolio);
    }

}
