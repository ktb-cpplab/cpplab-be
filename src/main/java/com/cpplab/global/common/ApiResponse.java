package com.cpplab.global.common;

import com.cpplab.global.common.code.BaseCode;
import com.cpplab.global.common.code.status.ErrorStatus;
import com.cpplab.global.common.code.status.SuccessStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess; // 생각해보기
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, SuccessStatus._OK.getCode() ,
                SuccessStatus._OK.getMessage(), result);
    }

    public static <T> ApiResponse<T> of(BaseCode code, T result) {
        return new ApiResponse<>(true, code.getReasonHttpStatus().code() ,
                code.getReasonHttpStatus().message(), result);
    }

    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }

    // 데이터 없이 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    // 기본 실패 응답 생성
    public static <T> ApiResponse<T> onFailure(T result) {
        return new ApiResponse<>(false, ErrorStatus._BAD_REQUEST.getCode(),
                ErrorStatus._BAD_REQUEST.getMessage(), result);
    }

    // 게시된 경우 응답 생성
    public static <T> ApiResponse<T> created(T result) {
        return new ApiResponse<>(true, SuccessStatus._CREATED.getCode(),
                SuccessStatus._CREATED.getMessage(), result);
    }

    // 삭제된 경우 응답 생성
    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(true, SuccessStatus._NO_CONTENT.getCode(),
                SuccessStatus._NO_CONTENT.getMessage(), null);
    }
}
