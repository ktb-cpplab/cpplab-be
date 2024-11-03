package com.cpplab.domain.mypage.entity.detail;

import com.cpplab.domain.mypage.entity.PortfolioEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;
    private String company; // 회사이름
    private String job; // 직무
    private LocalDate startDate; // 시작일자
    private LocalDate endDate; // 종료일자

    @ManyToOne
    @JoinColumn(name = "portfolioId")
    @JsonIgnore // 직렬화에서 제외하여 무한 참조 방지
    private PortfolioEntity portfolio;
}
