package com.example.prj_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private LocalDateTime expiresAt;
}
