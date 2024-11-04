package com.cpplab.domain.roadmap.entity;

import com.cpplab.domain.auth.entity.UserEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class StepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stepId;

    private String stepTitle;

    @ElementCollection
    private List<String> tasks; // 업무
}
