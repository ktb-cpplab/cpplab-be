package com.cpplab.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long userId;
    private String role;
    private String name;
    private String email;
    private String profileImage;
}
