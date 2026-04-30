package com.example.prj_management.service;

import com.example.prj_management.dto.request.ProjectRequest;
import com.example.prj_management.dto.response.ProjectResponse;
import com.example.prj_management.entity.Project;
import com.example.prj_management.entity.ProjectMember;
import com.example.prj_management.entity.User;
import com.example.prj_management.enums.Role;
import com.example.prj_management.repository.ProjectMemberRepository;
import com.example.prj_management.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserService userService;

    public ProjectResponse createProject(ProjectRequest request) {
        if (projectRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("project name existed");
        }

        User currUser = userService.findCurrUser();
        Project savedProject = projectRepository.save(
                Project.builder()
                        .name(request.getName())
                        .owner(currUser)
                        .build()
        );

        projectMemberRepository.save(
                ProjectMember.builder()
                        .project(savedProject)
                        .user(currUser)
                        .role(Role.OWNER)
                        .build()
        );

        return toResponse(savedProject);
    }

    public List<ProjectResponse> getAllProjects() {
        Long userId = userService.findCurrUser().getId();
        return projectRepository.findAllByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ProjectResponse getSingleProject(Long projectId) {
        Long userId = userService.findCurrUser().getId();
        Project project = projectRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return toResponse(project);
    }

    private ProjectResponse toResponse(Project p) {
        return ProjectResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .ownerId(p.getOwner().getId())
                .createdAt(p.getCreatedAt())
                .build();
    }

}
