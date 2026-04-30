package com.example.prj_management.dto.request;

import com.example.prj_management.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    private String description;
    private String title;
    private TaskStatus status;
}
