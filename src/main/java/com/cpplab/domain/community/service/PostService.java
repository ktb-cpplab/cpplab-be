package com.cpplab.domain.community.service;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.auth.repository.UserRepository;
import com.cpplab.domain.community.dto.PostRequest;
import com.cpplab.domain.community.dto.PostResponse;
import com.cpplab.domain.community.entity.LikeEntity;
import com.cpplab.domain.community.entity.PostEntity;
import com.cpplab.domain.community.repository.LikeRepository;
import com.cpplab.domain.community.repository.PostRepository;
import com.cpplab.global.common.code.status.ErrorStatus;
import com.cpplab.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;


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
    public PostEntity createPost(String userName, PostRequest.PostPutDto request) {

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_USER));

        PostEntity post = new PostEntity();
        post.setUser(user);
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setLikes(0L);
        post.setViews(0L);

        return postRepository.save(post);
    }

    // 게시글 조회
    public Page<PostEntity> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable); // 페이징을 적용해 Post 데이터베이스에서 데이터를 가져옵니다.

    }

    public PostEntity updatePost(String username, Long postId, PostRequest.PostPutDto request) {

        // 1. 게시글 존재 확인
        PostEntity updateEntity = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));

        // 2. 본인 게시물인지 확인
        if (!updateEntity.getUser().getUserName().equals(username)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
        // 제목과 내용 업데이트
        updateEntity.setTitle(request.title());
        updateEntity.setContent(request.content());

        // 변경 사항 저장
        return postRepository.save(updateEntity);
    }

    public void deletePost(String username,Long postId) {
        // 1. 게시글 존재 확인
        PostEntity deleteEntity = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));

        // 2. 본인 게시물인지 확인
        if (!deleteEntity.getUser().getUserName().equals(username)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        // 3. 게시글 삭제
        postRepository.delete(deleteEntity);
    }

    public void likePost(String username, Long postId, boolean likeStatus){


        // 1. 게시글 존재 확인
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));
        System.out.println("!!!!"+ username+postId+likeStatus);
        // 1. 본인이 좋아요 게시물에 대해서 삭제 및 생성
        // 2. true/false 여부에 따라서 게시물 총 조회수 값 (증가,감소) 여부 측정
        // 유저와 postId로 jpa로 바로 접근해서
        Optional<LikeEntity> existingLike = likeRepository.findByUserUserNameAndPostPostId(username, postId);
        System.out.println("aa"+existingLike);
        if (likeStatus){
            if (existingLike.isEmpty()) { // null이라면

                // 2. 유저가 있는지 판단
                UserEntity userEntity = userRepository.findByUserName(username)
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
            System.out.println("aaaa"+ username+postId+likeStatus);
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



}