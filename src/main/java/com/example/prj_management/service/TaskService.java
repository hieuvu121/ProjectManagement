package com.example.prj_management.service;

import com.example.prj_management.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;



}
