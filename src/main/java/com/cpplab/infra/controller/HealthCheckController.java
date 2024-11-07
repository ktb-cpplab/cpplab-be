package com.cpplab.infra.controller;

import com.cpplab.global.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/api/v1/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.onSuccess("health check");
    }


}
