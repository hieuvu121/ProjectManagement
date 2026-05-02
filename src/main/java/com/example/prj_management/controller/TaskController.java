package com.example.prj_management.controller;

import com.example.prj_management.dto.request.TaskRequest;
import com.example.prj_management.dto.response.TaskResponse;
import com.example.prj_management.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {
    private final TaskService taskService;

    @PreAuthorize("@authService.isMember(#projectId)")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@PathVariable Long projectId, @RequestBody TaskRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(projectId,request));
    }

    @PreAuthorize("@authService.isMember(#projectId)")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTask(@PathVariable Long projectId){
        return ResponseEntity.ok(taskService.getAllTask(projectId));
    }

    @PreAuthorize("@authService.isAssignTask(#taskId)")
    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long taskId, @RequestBody TaskRequest request){
        return ResponseEntity.ok(taskService.updateTask(taskId,request));
    }

    @PreAuthorize("@authService.isMember(#projectId)")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId){
        taskService.deleteTask(projectId, taskId);
        return ResponseEntity.noContent().build();
    }
}
