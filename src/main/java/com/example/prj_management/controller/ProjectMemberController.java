package com.example.prj_management.controller;

import com.example.prj_management.dto.request.AddMemberRequest;
import com.example.prj_management.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;

    @PreAuthorize("@authService.isOwner(#projectId)")
    @PostMapping
    public ResponseEntity<Void> addMember(@PathVariable Long projectId, @RequestBody AddMemberRequest request) {
        projectMemberService.addMember(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
