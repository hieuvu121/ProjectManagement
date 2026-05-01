package com.example.prj_management.service;

import com.example.prj_management.dto.request.AddMemberRequest;
import com.example.prj_management.entity.Project;
import com.example.prj_management.entity.ProjectMember;
import com.example.prj_management.entity.User;
import com.example.prj_management.repository.ProjectMemberRepository;
import com.example.prj_management.repository.ProjectRepository;
import com.example.prj_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public void addMember(Long projectId, AddMemberRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("project not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, request.getUserId())) {
            throw new IllegalArgumentException("user is already a member of this project");
        }

        projectMemberRepository.save(
                ProjectMember.builder()
                        .project(project)
                        .user(user)
                        .role(request.getRole())
                        .build()
        );
    }
}
