package com.cpplab.domain.community.controller;

import com.cpplab.domain.auth.dto.CustomOAuth2User;
import com.cpplab.domain.community.dto.PostRequest;
import com.cpplab.domain.community.dto.PostRequestDto;
import com.cpplab.domain.community.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {

    //createdPost
    // 게시글 작성
    @PostMapping("/")

    public ResponseEntity<String> createPost(@AuthenticationPrincipal CustomOAuth2User customUser, @RequestBody PostRequest.PostPutDto request){
//
        System.out.println("@@@" + customUser.getUsername());
//        PostService.
//        return ApiResponse.onSuccess(buyerService.updateMyPage(buyer, request));
//        PostEntity createdPost = postService.createPost(postRequestDto);
        return ResponseEntity.ok("good");
    }


    @GetMapping("/")
    public String my() {
        return "my";
    }


//    @GetMapping("/")


}
