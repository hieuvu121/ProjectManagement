package com.example.prj_management.repository;

import com.example.prj_management.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    Optional<Project> findByName(String name);

    @Query("select distinct p from Project p join p.members pm where pm.user.id = :userId")
    List<Project> findAllByUserId(@Param("userId") Long userId);

    @Query("select distinct p from Project p join p.members pm where pm.user.id=:userId and p.id=:projectId")
    Optional<Project> findByUserIdAndProjectId(@Param("userId") Long userId,@Param("projectId") Long projectId);
}
