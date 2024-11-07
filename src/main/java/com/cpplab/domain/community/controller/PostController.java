package com.cpplab.domain.community.controller;

import com.cpplab.domain.auth.dto.CustomOAuth2User;
import com.cpplab.domain.community.dto.DetailPostResponse;
import com.cpplab.domain.community.dto.PostRequest;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.domain.community.service.PostService;
import com.cpplab.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        PostEntity createPost = postService.createPost(customUser.getUserId(), request);
        return ApiResponse.onSuccess(createPost);
    }

    // 게시글 조회(페이징)
    @GetMapping("/all")
    public ApiResponse<Page<PostEntity>> getPosts(Pageable pageable){
        return ApiResponse.onSuccess(postService.getPosts(pageable));
    }

    // 게시글 상세 조회, 조회수+1
    @GetMapping("/{postId}/detail")
    public ApiResponse<DetailPostResponse> getPostDetail(@PathVariable Long postId){
        return ApiResponse.onSuccess(postService.getPostDetail(postId));
    }

    // 게시글 수정, 본인 게시물만 수정 가능
    @PutMapping("/{postId}")
    public ApiResponse<PostEntity> updatePost(@AuthenticationPrincipal CustomOAuth2User customUser, @PathVariable Long postId, @RequestBody PostRequest.PostPutDto request) {
        PostEntity updatedPost = postService.updatePost(customUser.getUserId(), postId, request);
        return ApiResponse.onSuccess(updatedPost);
    }

    // 게시글 삭제, 본인 게시물만 삭제 가능
    @DeleteMapping("/{postId}")
    public ApiResponse<String> deletePost(@AuthenticationPrincipal CustomOAuth2User customUser, @PathVariable Long postId) {
        postService.deletePost(customUser.getUserId(), postId);
        return ApiResponse.onSuccess("게시물이 성공적으로 삭제되었습니다.");
    }

    // 게시글 좋아요
    @PostMapping("/{postId}/like/{likeStatus}")
    public ApiResponse<String> likePost(@AuthenticationPrincipal CustomOAuth2User customUser, @PathVariable Long postId, @PathVariable String likeStatus) {
        postService.likePost(customUser.getUserId(), postId, Boolean.parseBoolean(likeStatus));
        return ApiResponse.onSuccess("게시글 좋아요를 눌렀습니다.");
    }




}
