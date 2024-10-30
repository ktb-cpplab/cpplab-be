package com.cpplab.domain.community.service;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.auth.repository.UserRepository;
import com.cpplab.domain.community.dto.PostRequest;
import com.cpplab.domain.community.dto.PostResponse;
import com.cpplab.domain.community.entity.PostEntity;
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

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;


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

        // postId로 게시글 조회 (없으면 예외 발생)
        PostEntity updateEntity = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_POST));

        // 본인 게시물인지 확인
        if (!updateEntity.getUser().getUserName().equals(username)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);

//            throw new UnauthorizedException("You are not authorized to update this post");
        }

        // 제목과 내용 업데이트
        updateEntity.setTitle(request.title());
        updateEntity.setContent(request.content());

        // 변경 사항 저장
        return postRepository.save(updateEntity);
    }



}
