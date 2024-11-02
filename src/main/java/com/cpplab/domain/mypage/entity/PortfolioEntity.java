package com.cpplab.domain.mypage.entity;

import com.cpplab.global.common.BaseEntity;
import com.cpplab.global.common.enums.Role;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class PortfolioEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioId;


    @Enumerated(EnumType.STRING)
    private Role role;    // 직위

    @ElementCollection
    private List<String> mainStack;


    // 나머지는 엔티티를 1개씩 더 만들어서 onetomany로 이어야 하며
    // 또한, 빌드 구조로 한번에 빌딩해서 put 하게 한다. 바탕화면 참고 할 것9

}
