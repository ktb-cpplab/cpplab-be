package com.cpplab.domain.mypage.entity;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.domain.mypage.entity.detail.ActivityEntity;
import com.cpplab.domain.mypage.entity.detail.CertificateEntity;
import com.cpplab.domain.mypage.entity.detail.CompanyEntity;
import com.cpplab.domain.mypage.entity.detail.EducationEntity;
import com.cpplab.global.common.BaseEntity;
import com.cpplab.global.common.enums.Rank;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class PortfolioEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioId;


    @Enumerated(EnumType.STRING)
    @Column(name = "userRank") // rank예약어가 있음
    private Rank rank;    // 직위

    @ElementCollection
    private List<String> mainStack; // 주 기술스택

    @ElementCollection
    private List<String> hopeCompany;
    private String hopeJob;

    @OneToOne
    @JoinColumn(name = "userId")
    private UserEntity user;

    @OneToMany
    @JoinColumn(name = "activityId")
    private List<ActivityEntity> activityEntity;

    @OneToMany
    @JoinColumn(name = "certificateId")
    private List<CertificateEntity> certificate;

    @OneToMany
    @JoinColumn(name = "companyId")
    private List<CompanyEntity> company;

    @OneToMany
    @JoinColumn(name = "educationId")
    private List<EducationEntity> education;

    // 나머지는 엔티티를 1개씩 더 만들어서 onetomany로 이어야 하며
    // 또한, 빌드 구조로 한번에 빌딩해서 put 하게 한다. 바탕화면 참고 할 것9

}
