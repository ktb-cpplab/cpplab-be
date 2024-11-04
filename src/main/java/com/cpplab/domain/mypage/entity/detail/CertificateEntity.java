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
public class CertificateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificateId;
    private String certificateName; // 자격증명
    private LocalDate date; // 취득일자

    @ManyToOne
    @JoinColumn(name = "portfolioId")
    @JsonIgnore // 직렬화에서 제외하여 무한 참조 방지
    private PortfolioEntity portfolio;
}
