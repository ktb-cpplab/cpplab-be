package com.cpplab.domain.mypage.dto.detail;

import java.time.LocalDate;

public record CertificateRequest(
        String certificateName,
        LocalDate date
) {
}
