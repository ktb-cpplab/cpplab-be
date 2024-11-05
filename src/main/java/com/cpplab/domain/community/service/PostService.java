package com.cpplab.domain.community.service;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.auth.repository.UserRepository;
import com.cpplab.domain.comment.dto.AllCommentResponse;
import com.cpplab.domain.comment.repository.CommentRepository;
import com.cpplab.domain.community.dto.DetailPostResponse;
import com.cpplab.domain.community.dto.PostRequest;
import com.cpplab.domain.community.entity.LikeEntity;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.domain.community.repository.LikeRepository;
import com.cpplab.domain.community.repository.PostRepository;
import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import com.cpplab.domain.roadmap.repository.RoadmapRepository;
import com.cpplab.global.common.code.status.ErrorStatus;
import com.cpplab.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final RoadmapRepository roadmapRepository;
    private final CommentRepository commentRepository;

//    public PostResponse createPost(String userName, PostRequest.PostPutDto request) {
//
//        UserEntity user = userRepository.findByUserName(userName)
//                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_USER));
//
//        PostEntity post = new PostEntity();
//        post.setUser(user);
//        post.setTitle(request.title());
//        post.setContent(request.content());
//        PostEntity savedPost = postRepository.save(post);
//
//        return PostResponse.builder()
//                .title(savedPost.getTitle())
//                .content(savedPost.getContent())
//                .views(0L)
//                .likes(0L)
//                .build();
//    }

    // 게시글 작성
    public PostEntity createPost(Long userId, PostRequest.PostPutDto request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_USER));

        PostEntity post = new PostEntity();
        post.setUser(user);
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setLikes(0L);
        post.setViews(0L);

        //해당 유저의 로드맵인지도 확인해야함

        // roadmapId가 존재하는 경우에만 설정
        if (request.roadmapId() != null) {
            RoadmapEntity roadmap = roadmapRepository.findById(request.roadmapId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_ROADMAP));

            if (roadmap.getUser().getUserId() != userId)
                throw new GeneralException(ErrorStatus._UNAUTHORIZED_ACCESS_ROADMAP);
            post.setRoadmap(roadmap);
        }
        return postRepository.save(post);
    }

    // 게시글 조회
    public Page<PostEntity> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable); // 페이징을 적용해 Post 데이터베이스에서 데이터를 가져옵니다.
    }

    // 게시글 상세 조회
//    @Transactional(readOnly = true)
//    public DetailPostResponse getPostDetail(Long postId) {
//        // 게시글 조회
//        PostEntity postEntity = postRepository.findById(postId)
//                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));
//
//        // 댓글 조회 및 변환
//        List<CommentResponse> comments = postEntity.getComments().stream()
//                .map(CommentResponse::from)
//                .collect(Collectors.toList());
//
//        // DetailPostResponse 생성 및 반환
//        return new DetailPostResponse(postEntity, comments);
//    }

    @Transactional(readOnly = true)
    public DetailPostResponse getPostDetail(Long postId) {
        // 게시글 조회
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));

        // 댓글 조회 및 변환
        List<AllCommentResponse> comments = commentRepository.findByPost_PostId(postId).stream()
                .map(AllCommentResponse::from)
                .collect(Collectors.toList());

        // DetailPostResponse 생성 및 반환
        return new DetailPostResponse(postEntity, comments);
    }

    public PostEntity updatePost(Long userId, Long postId, PostRequest.PostPutDto request) {
        // 1. 게시글 존재 확인
        PostEntity updateEntity = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));

        // 2. 본인 게시물인지 확인
        if (!updateEntity.getUser().getUserId().equals(userId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        // 제목과 내용 업데이트
        updateEntity.setTitle(request.title());
        updateEntity.setContent(request.content());

        // roadmapId가 존재하는 경우에만 설정
        if (request.roadmapId() != null) {
            RoadmapEntity roadmap = roadmapRepository.findById(request.roadmapId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_ROADMAP));

            if (roadmap.getUser().getUserId() != userId)
                throw new GeneralException(ErrorStatus._UNAUTHORIZED_ACCESS_ROADMAP);
            updateEntity.setRoadmap(roadmap);
        }

        // 변경 사항 저장
        return postRepository.save(updateEntity);
    }

    public void deletePost(Long userId,Long postId) {
        // 1. 게시글 존재 확인
        PostEntity deleteEntity = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));

        // 2. 본인 게시물인지 확인
        if (!deleteEntity.getUser().getUserId().equals(userId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        // 3. 게시글 삭제
        postRepository.delete(deleteEntity);

        // 댓글도 전부 삭제되는지 확인할 것.
    }

    public void likePost(Long userId, Long postId, boolean likeStatus){

        // 1. 게시글 존재 확인
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));
        // 1. 본인이 좋아요 게시물에 대해서 삭제 및 생성
        // 2. true/false 여부에 따라서 게시물 총 조회수 값 (증가,감소) 여부 측정
        // 유저와 postId로 jpa로 바로 접근해서
        Optional<LikeEntity> existingLike = likeRepository.findByUserUserIdAndPostPostId(userId, postId);
        System.out.println("aa"+existingLike);
        if (likeStatus){
            if (existingLike.isEmpty()) { // null이라면

                // 2. 유저가 있는지 판단
                UserEntity userEntity = userRepository.findById(userId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_USER));

                // 좋아요 엔티티 생성과 저장
                LikeEntity newLike = new LikeEntity();
                newLike.setUser(userEntity);
                newLike.setPost(postEntity);
                likeRepository.save(newLike);

                // 게시물 조회수 증가
                postEntity.setViews(postEntity.getViews() + 1);
                postRepository.save(postEntity);
            }
        } else if (!likeStatus) {
            // 4. likeStatus가 false인 경우
            System.out.println("aaaa"+ userId+postId+likeStatus);
            existingLike.ifPresent(like -> {
                likeRepository.delete(like);

                // 게시물 조회수 감소
                postEntity.setViews(postEntity.getViews() - 1);
                postRepository.save(postEntity);
            });
        }
        else {
            // 에러 처리
        }
    }

//    public static CommentResponse from(CommentEntity comment) {
//        return new CommentResponse(
//                comment.getCommentId(),
//                comment.getName(),
//                comment.getContent(),
//                comment.getRank()
//        );
//    }

}
