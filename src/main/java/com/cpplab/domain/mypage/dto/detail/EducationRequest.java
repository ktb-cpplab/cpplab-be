package com.cpplab.domain.mypage.dto.detail;

public record EducationRequest(
        String university,
        String department,
        Double gpa,
        Double gpaMax
) {
}
