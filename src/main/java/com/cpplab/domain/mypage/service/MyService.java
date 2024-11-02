package com.cpplab.domain.mypage.service;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.auth.repository.UserRepository;
import com.cpplab.domain.mypage.dto.UserInfoRequest;
import com.cpplab.global.common.code.status.ErrorStatus;
import com.cpplab.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyService {

    private final UserRepository userRepository;

    public UserEntity getUserInfo(String username){
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_USER));
    }

    public UserEntity putUserInfo(String username, UserInfoRequest request) {
        UserEntity userEntity = userRepository.findByUserName(username)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_USER));

        userEntity.setName(request.name()); // 새로운 닉네임 업데이트
        userRepository.save(userEntity); // 변경사항 저장
        return userEntity;
    }
}
