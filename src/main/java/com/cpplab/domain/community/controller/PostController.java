package com.cpplab.domain.community.controller;

import com.cpplab.domain.auth.dto.CustomOAuth2User;
import com.cpplab.domain.community.dto.PostRequest;
import com.cpplab.domain.community.dto.PostResponse;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.domain.community.service.PostService;
import com.cpplab.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;

//    // 게시글 작성
//    @PostMapping("")
//    public ApiResponse<PostResponse> createPost(@AuthenticationPrincipal CustomOAuth2User customUser, @RequestBody PostRequest.PostPutDto request){
//        PostResponse postResponse = postService.createPost(customUser.getUsername(), request);
//        return ApiResponse.onSuccess(postResponse);
//    }

    // 게시글 작성, 사용자 정보까지 반환
    @PostMapping("")
    public ApiResponse<PostEntity> createPost(@AuthenticationPrincipal CustomOAuth2User customUser, @RequestBody PostRequest.PostPutDto request){
        PostEntity createEntity = postService.createPost(customUser.getUsername(), request);
        return ApiResponse.onSuccess(createEntity);
    }

    // 게시글 조회(페이징)
    @GetMapping("/all")
    public ApiResponse<Page<PostEntity>> getPosts(Pageable pageable){
        return ApiResponse.onSuccess(postService.getPosts(pageable));
    }

    // 게시글 수정, 본인 게시물만 수정 가능
    @PutMapping("/{postId}")
    public ApiResponse<PostEntity> updatePost(@AuthenticationPrincipal CustomOAuth2User customUser, @PathVariable Long postId, @RequestBody PostRequest.PostPutDto request) {
        PostEntity updatedPost = postService.updatePost(customUser.getUsername(), postId, request);
        return ApiResponse.onSuccess(updatedPost);
    }


    // 게시글 삭제
    // 게시글 좋아요





}
