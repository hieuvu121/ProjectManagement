package com.example.prj_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private LocalDateTime expiresAt;
}
