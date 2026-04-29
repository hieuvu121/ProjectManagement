package com.example.prj_management.controller;

import com.example.prj_management.dto.request.ProjectRequest;
import com.example.prj_management.dto.response.ProjectResponse;
import com.example.prj_management.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(@PathVariable Long userId) {
        return ResponseEntity.ok(projectService.getAllProjects(userId));
    }

    @GetMapping("/user/{userId}/{projectId}")
    public ResponseEntity<ProjectResponse> getSingleProject(@PathVariable Long userId, @PathVariable Long projectId){
        return ResponseEntity.ok(projectService.getSingleProjects(userId,projectId));
    }
}
