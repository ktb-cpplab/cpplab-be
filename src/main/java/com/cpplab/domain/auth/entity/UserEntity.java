package com.cpplab.domain.auth.entity;

import com.cpplab.global.common.BaseEntity;
import com.cpplab.global.common.enums.Provider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String ninkname; // 닉네임
    private String email;
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Provider provider; // KAKAO, NAVER

}
