package com.cpplab.domain.mypage.dto;

import com.cpplab.domain.mypage.dto.detail.*;
import com.cpplab.global.common.enums.Rank;

import java.util.List;

public record PortfolioRequest(
    Rank rank,
    List<String> mainStack,
    List<String> hopeCompany,
    String hopeJob,
    List<ActivityRequest> activities,
    List<CertificateRequest> certificates,
    List<CompanyRequest> companies,
    List<EducationRequest> educations,
    List<PrizeRequest> prizes,
    List<ProjectRequest> projects
) {
}
