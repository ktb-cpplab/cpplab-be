package com.cpplab.test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class MyController {

    private final MyService myService;

    @Autowired
    public MyController(MyService myService) {
        this.myService = myService;
    }

    @GetMapping("/success")
    public ApiResponse<String> success() {
        return myService.getSuccessResponse();
    }

    @GetMapping("/created")
    public ApiResponse<String> created() {
        return myService.getCreatedResponse();
    }

    @GetMapping("/no-content")
    public ApiResponse<String> noContent() {
        return myService.getNoContentResponse();
    }

    @GetMapping("/failure")
    public ApiResponse<String> failure() {
        return myService.getFailureResponse();
    }

    @GetMapping("/bad-request")
    public ApiResponse<String> badRequest() {
        return myService.getBadRequestResponse();
    }

}
