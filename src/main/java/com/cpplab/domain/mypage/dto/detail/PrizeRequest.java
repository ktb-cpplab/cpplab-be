package com.cpplab.domain.mypage.dto.detail;

import java.time.LocalDate;

public record PrizeRequest(
        String title,
        String description,
        LocalDate date
) {
}
