package com.cpplab.domain.comment.controller;

import com.cpplab.domain.auth.dto.CustomOAuth2User;
import com.cpplab.domain.comment.dto.CommentRequest;
import com.cpplab.domain.comment.entity.CommentEntity;
import com.cpplab.domain.comment.service.CommentService;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{postId}/comment")
    public ApiResponse<CommentEntity> createComment(@AuthenticationPrincipal CustomOAuth2User customUser, @PathVariable Long postId, @RequestBody CommentRequest request){
        CommentEntity createComment = commentService.createComment(customUser.getUsername(), postId, request);
        return ApiResponse.onSuccess(createComment);
    }

    // 댓글 수정
//    @PutMapping("/{postId}/comment")

    // 댓글 삭제
//    @DeleteMapping("/{postId}/comment")

}
