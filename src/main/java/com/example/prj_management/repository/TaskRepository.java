package com.example.prj_management.repository;

import com.example.prj_management.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {
}
