package com.cpplab.domain.mypage.entity.detail;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PrizeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prizeId;

}
