package com.taskmanager.task_manager_service.repository;

import com.taskmanager.task_manager_service.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
