package com.cpplab.global.common.code;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ErrorReasonDTO(
        HttpStatus httpStatus,
        boolean isSuccess,
        String code,
        String message
) {
}