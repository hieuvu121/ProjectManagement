package com.example.prj_management.repository;

import com.example.prj_management.entity.TaskEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskEventRepository extends JpaRepository<TaskEvent,Long> {
}
