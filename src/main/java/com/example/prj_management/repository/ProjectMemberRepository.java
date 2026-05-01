package com.example.prj_management.repository;

import com.example.prj_management.entity.ProjectMember;
import com.example.prj_management.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember,Long> {

    @Query("SELECT COUNT(pm) > 0 FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.user.id = :userId")
    boolean existsByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    @Query("SELECT COUNT(pm) > 0 FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.user.id = :userId AND pm.role = :role")
    boolean existsByProjectIdAndUserIdAndRole(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("role") Role role);
}
