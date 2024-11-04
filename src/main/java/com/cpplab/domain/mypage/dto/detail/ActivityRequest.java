package com.cpplab.domain.mypage.dto.detail;


import java.time.LocalDate;

public record ActivityRequest(
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate
) {
}
