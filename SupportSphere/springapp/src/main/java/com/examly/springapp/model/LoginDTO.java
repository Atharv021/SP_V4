package com.examly.springapp.model;

import lombok.Getter;
import lombok.Setter;

// Sent back to Angular after a successful login (SRS: LoginDTO).
@Getter
@Setter
public class LoginDTO {

    private String token;
    private String username;
    private String userRole;
    private Long userId;

    public LoginDTO() {
    }

    public LoginDTO(String token, String username, String userRole, Long userId) {
        this.token = token;
        this.username = username;
        this.userRole = userRole;
        this.userId = userId;
    }
}
