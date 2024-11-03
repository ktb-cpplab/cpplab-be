package com.cpplab.domain.comment.service;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.auth.repository.UserRepository;
import com.cpplab.domain.comment.dto.CommentRequest;
import com.cpplab.domain.comment.dto.CommentResponse;
import com.cpplab.domain.comment.entity.CommentEntity;
import com.cpplab.domain.comment.repository.CommentRepository;
import com.cpplab.domain.community.dto.PostRequest;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.domain.community.repository.PostRepository;
import com.cpplab.domain.mypage.entity.PortfolioEntity;
import com.cpplab.domain.mypage.repository.PortfolioRepository;
import com.cpplab.global.common.code.status.ErrorStatus;
import com.cpplab.global.common.enums.Rank;
import com.cpplab.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PortfolioRepository portfolioRepository;

    @Transactional
    public CommentResponse createComment(Long userId, Long postId, CommentRequest request) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_USER));

        // 1. 게시글 존재 확인
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));

        // 2. 포트폴리오, 랭크
        Rank userRank = portfolioRepository.findByUser_UserId(userId)
                .map(PortfolioEntity::getRank)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_PORTFOLIO));

        CommentEntity comment = CommentEntity.builder()
                .nickName(userEntity.getNickName())
                .rank(userRank) // 직위 설정
                .content(request.content()) // 댓글 내용 설정 (예시로 getContent 메서드 사용)
                .post(postEntity) // 해당 게시글 설정, postid로 바꾸기
                .user(userEntity)
                .build();
        CommentEntity savedComment = commentRepository.save(comment);

        return toCommentResponse(savedComment);
    }

    public CommentResponse updateComment(Long userId, Long postId, Long commentId, CommentRequest request) {
        // 1. 댓글 존재 확인
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_COMMENT));

        // 2. 게시글 존재 확인
        if (!postRepository.existsById(postId)) {
            throw new GeneralException(ErrorStatus._NOT_FOUND_POST);
        }
        // 3. 본인 댓글인지 확인
        else if (comment.getUser().getUserId() != userId) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        // 4. 댓글 수정
        comment.setContent(request.content());
        CommentEntity updateComment = commentRepository.save(comment);
        return toCommentResponse(updateComment);
    }

    // CommentEntity를 CommentResponse로 변환하는 메서드
    private CommentResponse toCommentResponse(CommentEntity commentEntity) {
        return CommentResponse.builder()
                .commentId(commentEntity.getCommentId())
                .nickName(commentEntity.getNickName())
                .content(commentEntity.getContent())
                .rank(commentEntity.getRank().toString())
                .userId(commentEntity.getUser().getUserId())
                .postId(commentEntity.getPost().getPostId())
                .createdAt(commentEntity.getCreatedAt())
                .modifiedAt(commentEntity.getModifiedAt())
                .build();
    }

    public void deleteComment(Long userId, Long postId, Long commentId) {
        // 1. 댓글 존재 확인
        CommentEntity deleteComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_COMMENT));

        // 2. 게시글 존재 확인
        if (!postRepository.existsById(postId)) {
            throw new GeneralException(ErrorStatus._NOT_FOUND_POST);
        }
        // 3. 본인 댓글인지 확인
        else if (deleteComment.getUser().getUserId().equals(userId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
        // 4. 댓글 삭제
        commentRepository.delete(deleteComment);
    }


}
