package com.cpplab.global.common.exception;

import lombok.Getter;

@Getter
public class InvalidProductStateException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidProductStateException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}