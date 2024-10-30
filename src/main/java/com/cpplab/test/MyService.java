package com.cpplab.test;


import com.cpplab.global.common.code.status.ErrorStatus;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    // 성공 응답 시뮬레이션
    public ApiResponse<String> getSuccessResponse() {
        return ApiResponse.onSuccess("Operation successful");
    }

    // 생성된 응답 시뮬레이션
    public ApiResponse<String> getCreatedResponse() {
        return ApiResponse.created("Resource created successfully");
    }

    // 내용 없음 응답 시뮬레이션
    public ApiResponse<String> getNoContentResponse() {
        return ApiResponse.noContent();
    }

    // 실패 응답 (커스텀 메시지 포함)
    public ApiResponse<String> getFailureResponse() {
        return ApiResponse.onFailure("ERR001", "Operation failed due to XYZ issue");
    }

    // 잘못된 요청 응답 시뮬레이션
    public ApiResponse<String> getBadRequestResponse() {
        return ApiResponse.onFailure(ErrorStatus._BAD_REQUEST.getMessage());
    }

}
