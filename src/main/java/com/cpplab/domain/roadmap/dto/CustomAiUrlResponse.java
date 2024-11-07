package com.cpplab.domain.roadmap.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomAiUrlResponse {
    private String hopeJob;
    private String projectLevel;
    private List<String> projectStack;
    private String projectTitle;
    private String projectDescription;
}
