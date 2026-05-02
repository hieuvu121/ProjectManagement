package com.example.prj_management.service;

import com.example.prj_management.dto.request.TaskRequest;
import com.example.prj_management.dto.response.TaskResponse;
import com.example.prj_management.entity.Project;
import com.example.prj_management.entity.Task;
import com.example.prj_management.entity.TaskAssign;
import com.example.prj_management.entity.User;
import com.example.prj_management.enums.TaskStatus;
import com.example.prj_management.repository.ProjectRepository;
import com.example.prj_management.repository.TaskAssignRepository;
import com.example.prj_management.repository.TaskRepository;
import com.example.prj_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.prj_management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskAssignRepository taskAssignRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public TaskResponse createTask(Long projectId, TaskRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Task task = taskRepository.save(
                Task.builder()
                        .project(project)
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        List<Long> assigneeIds = new ArrayList<>();
        if (request.getAssigneeIds() != null) {
            for (Long userId : request.getAssigneeIds()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                taskAssignRepository.save(
                        TaskAssign.builder()
                                .task(task)
                                .user(user)
                                .build()
                );
                assigneeIds.add(userId);
            }
        }

        return toResponse(task, assigneeIds);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTask(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }
        return taskRepository.findAllByProjectId(projectId).stream()
                .map(task -> toResponse(task, task.getAssignees().stream()
                        .map(a -> a.getUser().getId())
                        .toList()))
                .toList();
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("no task found"));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        if (request.getAssigneeIds() != null) {
            //current assignees
            Map<Long, TaskAssign> currentMap = task.getAssignees().stream()
                    .collect(Collectors.toMap(a -> a.getUser().getId(), a -> a));

            Set<Long> requestedIds = Set.copyOf(request.getAssigneeIds());

            //fetch all requested users
            List<User> requestedUsers = userRepository.findAllById(requestedIds);

            if (requestedUsers.size() != requestedIds.size()) {
                throw new ResourceNotFoundException("One or more users not found");
            }

            //add users
            List<TaskAssign> toAdd = requestedUsers.stream()
                    .filter(u -> !currentMap.containsKey(u.getId()))
                    .map(u -> TaskAssign.builder().task(task).user(u).build())
                    .toList();
            taskAssignRepository.saveAll(toAdd);

            //remove users
            for (Map.Entry<Long, TaskAssign> entry : currentMap.entrySet()) {
                if (!requestedIds.contains(entry.getKey())) {
                    taskAssignRepository.delete(entry.getValue());
                }
            }
        }

        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);

        List<Long> updatedAssigneeIds = task.getAssignees().stream()
                .map(a -> a.getUser().getId())
                .toList();

        return toResponse(task, updatedAssigneeIds);
    }

    @Transactional
    public void deleteTask(Long projectId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("task not found"));

        if (!task.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException("task not found in this project");
        }

        taskAssignRepository.deleteAll(task.getAssignees());
        taskRepository.delete(task);
    }

    private TaskResponse toResponse(Task task, List<Long> assigneeIds) {
        return TaskResponse.builder()
                .id(task.getId())
                .projectId(task.getProject().getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .assigneeIds(assigneeIds)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
