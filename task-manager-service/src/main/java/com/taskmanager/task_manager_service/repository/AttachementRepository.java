package com.taskmanager.task_manager_service.repository;

import com.taskmanager.task_manager_service.entity.Attachement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachementRepository extends JpaRepository<Attachement, Long> {
}
