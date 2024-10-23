package com.cpplab.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String role;
    private String name;
    private String username;
    private String email;
    private String image;
}
