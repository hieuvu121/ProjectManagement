package com.example.prj_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class ProjectResponse {
    private String name;
    private LocalDateTime createdAt;
}
