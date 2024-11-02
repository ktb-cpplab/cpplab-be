package com.cpplab.domain.comment.service;

import com.cpplab.domain.comment.dto.CommentRequest;
import com.cpplab.domain.comment.entity.CommentEntity;
import com.cpplab.domain.comment.repository.CommentRepository;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.domain.community.repository.PostRepository;
import com.cpplab.global.common.code.status.ErrorStatus;
import com.cpplab.global.common.enums.Rank;
import com.cpplab.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public CommentEntity createComment(String username, Long postId, CommentRequest request) {

        // 1. 게시글 존재 확인
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));

        // 2. 포트폴리오, 랭크
        // 에러찾기, String username말고 id로 찾게 바꾸기
        Rank userRank = portfolioRepository.findByUserUsername(username)
                .map(PortfolioEntity::getRank) // PortfolioEntity에서 Rank 가져오기
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_PORTFOLIO));

        CommentEntity comment = CommentEntity.builder()
                .name(username) // 소셜닉네임 설정
                .content(request.content()) // 댓글 내용 설정 (예시로 getContent 메서드 사용)
                .post(postEntity) // 해당 게시글 설정
                .build();

        return commentRepository.save(comment); // 저장 후 반환



}
