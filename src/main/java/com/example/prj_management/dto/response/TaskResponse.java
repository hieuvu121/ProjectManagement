package com.example.prj_management.dto.response;

import com.example.prj_management.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private List<Long> assigneeIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
