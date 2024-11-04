package com.cpplab.domain.mypage.dto.detail;

import java.time.LocalDate;

public record CompanyRequest(
        String company,
        String job,
        LocalDate startDate,
        LocalDate endDate
) {
}
