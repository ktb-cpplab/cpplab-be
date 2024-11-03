package com.cpplab.domain.mypage.dto.detail;

import java.util.List;

public record ProjectRequest(
        String title,
        String description,
        List<String> stacks
) {
}
