package com.example.prj_management.repository;

import com.example.prj_management.entity.TaskAssign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAssignRepository extends JpaRepository<TaskAssign, Long> {
    boolean existsByTaskIdAndUserId(Long taskId, Long userId);
}
