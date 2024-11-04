package com.cpplab.domain.mypage.entity.detail;

import com.cpplab.domain.mypage.entity.PortfolioEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long educationId;
    private String university; // 학교
    private String department; // 학과
    private Double gpa; // 학점
    private Double gpaMax; // 최대학점

    @ManyToOne
    @JoinColumn(name = "portfolioId")
    @JsonIgnore // 직렬화에서 제외하여 무한 참조 방지
    private PortfolioEntity portfolio;

}
