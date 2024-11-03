package com.cpplab.domain.mypage.service;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.auth.repository.UserRepository;
import com.cpplab.domain.mypage.dto.UserInfoRequest;
import com.cpplab.domain.mypage.entity.PortfolioEntity;
import com.cpplab.domain.mypage.repository.PortfolioRepository;
import com.cpplab.global.common.code.status.ErrorStatus;
import com.cpplab.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PortfolioEntity putPortfolio(Long userId, PortfolioEntity request){
        // 유저의 포트폴리오를 조회
        PortfolioEntity portfolio = portfolioRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_PORTFOLIO));

        // 필요한 필드 업데이트
        portfolio.setRank(request.getRank());
        portfolio.setMainStack(request.getMainStack());
        portfolio.setHopeCompany(request.getHopeCompany());
        portfolio.setHopeJob(request.getHopeJob());

        // 서브 엔티티 업데이트 (활동, 자격증, 회사, 교육 정보 등)
        portfolio.setActivityEntity(request.getActivityEntity());
        portfolio.setCertificate(request.getCertificate());
        portfolio.setCompany(request.getCompany());
        portfolio.setEducation(request.getEducation());

        // 변경된 포트폴리오 저장
        return portfolioRepository.save(portfolio);
    }

}
