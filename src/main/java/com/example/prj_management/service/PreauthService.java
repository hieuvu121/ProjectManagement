package com.example.prj_management.service;

import com.example.prj_management.enums.Role;
import com.example.prj_management.exception.ResourceNotFoundException;
import com.example.prj_management.repository.ProjectMemberRepository;
import com.example.prj_management.repository.ProjectRepository;
import com.example.prj_management.repository.TaskAssignRepository;
import com.example.prj_management.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("authService")
public class PreauthService {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final TaskAssignRepository taskAssignRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    public boolean isMember(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }
        Long userId = userService.findCurrUser().getId();
        return projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    public boolean isOwner(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }
        Long userId = userService.findCurrUser().getId();
        return projectMemberRepository.existsByProjectIdAndUserIdAndRole(projectId, userId, Role.OWNER);
    }

    public boolean isAssignTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found");
        }
        Long userId = userService.findCurrUser().getId();
        return taskAssignRepository.existsByTaskIdAndUserId(taskId, userId);
    }
}
