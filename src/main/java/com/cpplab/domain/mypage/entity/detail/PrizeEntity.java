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
public class PrizeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prizeId;
    private String title; // 수상명
    private String description; // 1줄 설명
    private LocalDate date; // 수상일시

    @ManyToOne
    @JoinColumn(name = "portfolioId")
    @JsonIgnore // 직렬화에서 제외하여 무한 참조 방지
    private PortfolioEntity portfolio;
}
