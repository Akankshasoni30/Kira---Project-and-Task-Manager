package com.taskmanager.task_manager_service.controller;

import com.taskmanager.task_manager_service.dto.TaskResponseDTO;
import com.taskmanager.task_manager_service.entity.Project;
import com.taskmanager.task_manager_service.entity.Task;
import com.taskmanager.task_manager_service.entity.User;
import com.taskmanager.task_manager_service.repository.ProjectRepository;
import com.taskmanager.task_manager_service.repository.TaskRepository;
import com.taskmanager.task_manager_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.taskmanager.task_manager_service.service.EmailService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;

    //  Create Task - without email service integration
//    @PostMapping
//    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody Task task) {
//        task.setCreatedAt(LocalDateTime.now());
//        task.setUpdatedAt(LocalDateTime.now());
//
//        if (task.getProject() != null && task.getProject().getId() != null) {
//            Project project = projectRepository.findById(task.getProject().getId())
//                    .orElseThrow(() -> new RuntimeException("Project not found"));
//            task.setProject(project);
//        }
//
//        if (task.getAssignee() != null && task.getAssignee().getId() != null) {
//            User assignee = userRepository.findById(task.getAssignee().getId())
//                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
//            task.setAssignee(assignee);
//        }
//
//        Task savedTask = taskRepository.save(task);
//        return ResponseEntity.ok(mapToDTO(savedTask));
//    }
    //create task - with email service integration
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        if (task.getProject() != null && task.getProject().getId() != null) {
            Project project = projectRepository.findById(task.getProject().getId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            task.setProject(project);
        }

        if (task.getAssignee() != null && task.getAssignee().getId() != null) {
            User assignee = userRepository.findById(task.getAssignee().getId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
        }

        Task savedTask = taskRepository.save(task);

        
        if (savedTask.getAssignee() != null && savedTask.getAssignee().getEmail() != null) {
            emailService.sendTaskAssignedEmail(
                savedTask.getAssignee().getEmail(),
                savedTask.getAssignee().getName(),
                savedTask.getTitle(),
                savedTask.getProject() != null ? savedTask.getProject().getName() : "General"
            );
        }

        return ResponseEntity.ok(mapToDTO(savedTask));
    }


    
    @GetMapping
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

 
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> ResponseEntity.ok(mapToDTO(task)))
                .orElse(ResponseEntity.notFound().build());
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, @RequestBody Task updated) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(updated.getTitle());
                    task.setDescription(updated.getDescription());
                    task.setDueDate(updated.getDueDate());
                    task.setStatus(updated.getStatus());
                    task.setPriority(updated.getPriority());
                    task.setUpdatedAt(LocalDateTime.now());
                    Task saved = taskRepository.save(task);
                    return ResponseEntity.ok(mapToDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

 
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByProject(@PathVariable Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<TaskResponseDTO> dtoList = tasks.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

  
    private TaskResponseDTO mapToDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getProject() != null ? task.getProject().getName() : null,
                task.getAssignee() != null ? task.getAssignee().getName() : null,
                task.getAssignee() != null ? task.getAssignee().getEmail() : null,
                task.getDueDate()
        );
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        return taskRepository.findById(id)
                .map(task -> {
                    task.setStatus(newStatus);
                    task.setUpdatedAt(LocalDateTime.now());
                    Task saved = taskRepository.save(task);
                    return ResponseEntity.ok(mapToDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
