package com.cpplab.domain.mypage.entity;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.domain.mypage.entity.detail.*;
import com.cpplab.global.common.BaseEntity;
import com.cpplab.global.common.enums.Rank;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityEntity> activities;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertificateEntity> certificates;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompanyEntity> companies;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EducationEntity> educations;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrizeEntity> prizes;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectEntity> projects;

}
