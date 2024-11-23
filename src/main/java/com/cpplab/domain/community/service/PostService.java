package com.cpplab.domain.community.service;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.auth.repository.UserRepository;
import com.cpplab.domain.comment.dto.AllCommentResponse;
import com.cpplab.domain.comment.repository.CommentRepository;
import com.cpplab.domain.community.dto.DetailPostResponse;
import com.cpplab.domain.community.dto.PostRequest;
import com.cpplab.domain.community.dto.PostResponse;
import com.cpplab.domain.community.entity.LikeEntity;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.domain.community.repository.LikeRepository;
import com.cpplab.domain.community.repository.PostRepository;
import com.cpplab.domain.mypage.entity.PortfolioEntity;
import com.cpplab.domain.mypage.repository.PortfolioRepository;
import com.cpplab.domain.roadmap.dto.RoadmapResponse;
import com.cpplab.domain.roadmap.entity.roadmap.RoadmapEntity;
import com.cpplab.domain.roadmap.repository.RoadmapRepository;
import com.cpplab.global.common.code.status.ErrorStatus;
import com.cpplab.global.common.enums.Rank;
import com.cpplab.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final RoadmapRepository roadmapRepository;
    private final CommentRepository commentRepository;
    private final PortfolioRepository portfolioRepository;

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
    public Page<PostResponse> getPosts(Long userId, Pageable pageable) {

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("postId").descending() // postId 기준 내림차순
        );

        return postRepository.findAll(sortedPageable).map(post -> {
            boolean isLike = likeRepository.existsByUserUserIdAndPostPostId(userId, post.getPostId());
            Rank rank = portfolioRepository.findByUser(post.getUser())
                    .map(PortfolioEntity::getRank)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_PORTFOLIO));

            return PostResponse.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .views(post.getViews())
                    .likes(post.getLikes())
                    .commentCount(post.getCommentCount())
                    .isLiked(isLike)
                    .rank(rank)
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .user(PostResponse.PostUserResponse.builder()
                            .userId(post.getUser().getUserId())
                            .nickName(post.getUser().getNickName())
                            .profileImage(post.getUser().getProfileImage())
                            .build())
                    .build();
        });
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

    @Transactional
    public DetailPostResponse getPostDetail(Long userId, Long postId) {
        // 게시글 조회
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));

        postEntity.setViews(postEntity.getViews() + 1);
        postRepository.save(postEntity); // 변경 사항 저장

        // 좋아요 여부 확인
        boolean isLiked = likeRepository.existsByUserUserIdAndPostPostId(userId, postId);

        // 작성자 정보
        UserEntity user = postEntity.getUser();
        DetailPostResponse.PostUserResponse userResponse = DetailPostResponse.PostUserResponse.builder()
                .userId(user.getUserId())
                .nickName(user.getNickName())
                .profileImage(user.getProfileImage())
                .build();

        // Roadmap 정보
        RoadmapResponse roadmapResponse = null;
        if (postEntity.getRoadmap() != null) {
            RoadmapEntity roadmapEntity = postEntity.getRoadmap();
            roadmapResponse = RoadmapResponse.from(roadmapEntity);
        }

        // PortfolioEntity에서 Rank 조회
        Rank rank = portfolioRepository.findByUser(postEntity.getUser())
                .map(PortfolioEntity::getRank)
                .orElse(null); // 포트폴리오가 없을 경우 null 반환

        // 댓글 조회 및 변환
        List<AllCommentResponse> comments = commentRepository.findByPost_PostId(postId).stream()
                .map(AllCommentResponse::from)
                .collect(Collectors.toList());

        // DetailPostResponse 생성 및 반환
        return DetailPostResponse.builder()
                .postId(postEntity.getPostId())
                .title(postEntity.getTitle())
                .content(postEntity.getContent())
                .views(postEntity.getViews())
                .likes(postEntity.getLikes())
                .commentCount(postEntity.getCommentCount())
                .isLike(isLiked)
                .rank(rank)
                .createdAt(postEntity.getCreatedAt())
                .modifiedAt(postEntity.getModifiedAt())
                .user(userResponse)
                .roadmap(roadmapResponse)
                .comments(comments)
                .build();
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

    @Transactional
    public void deletePost(Long userId,Long postId) {
        // 1. 게시글 존재 확인
        PostEntity deletePostEntity = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));

        // 2. 본인 게시물인지 확인
        if (!deletePostEntity.getUser().getUserId().equals(userId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        // 3. 관련 LikeEntity 삭제
        likeRepository.deleteByPost(deletePostEntity);

        // 4. 관련 CommentEntity 삭제
        commentRepository.deleteByPost(deletePostEntity);

        // 3. 게시글 삭제
        postRepository.delete(deletePostEntity);
    }

    @Transactional
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

                // 게시물 좋아요 증가
                postEntity.setLikes(postEntity.getLikes() + 1);
                postRepository.save(postEntity);
            }
        } else if (!likeStatus) {
            // 4. likeStatus가 false인 경우
            System.out.println("aaaa"+ userId+postId+likeStatus);
            existingLike.ifPresent(like -> {
                likeRepository.delete(like);

                // 게시물 좋아요 감소
                postEntity.setLikes(postEntity.getLikes() - 1);
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
