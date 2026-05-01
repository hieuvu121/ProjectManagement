package com.example.prj_management.service;

import com.example.prj_management.enums.Role;
import com.example.prj_management.repository.ProjectMemberRepository;
import com.example.prj_management.repository.TaskAssignRepository;
import com.example.prj_management.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("authService")
public class PreauthService {
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskAssignRepository taskAssignRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    public boolean isMember(Long projectId){
        Long userId = userService.findCurrUser().getId();
        return projectMemberRepository.existsByProjectIdAndUserId(projectId,userId);
    }

    public boolean isOwner(Long projectId){
        Long userId=userService.findCurrUser().getId();
        return projectMemberRepository.existsByProjectIdAndUserIdAndRole(projectId,userId, Role.OWNER);
    }

    public boolean isAssignTask(Long taskId) {
        Long userId = userService.findCurrUser().getId();
        return taskAssignRepository.existsByTaskIdAndUserId(taskId, userId);
    }
}
