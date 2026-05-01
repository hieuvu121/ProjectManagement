package com.example.prj_management.repository;

import com.example.prj_management.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long> {
    Optional<Task> findByTitle(String title);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId")
    List<Task> findAllByProjectId(@Param("projectId") Long projectId);
}
