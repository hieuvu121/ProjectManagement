package com.example.prj_management.controller;

import com.example.prj_management.dto.request.ProjectRequest;
import com.example.prj_management.dto.response.ProjectResponse;
import com.example.prj_management.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @PreAuthorize("@authService.isMember(#projectId)")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getSingleProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getSingleProject(projectId));
    }
}
