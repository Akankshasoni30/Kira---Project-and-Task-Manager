package com.taskmanager.task_manager_service.controller;

import com.taskmanager.task_manager_service.entity.Project;
import com.taskmanager.task_manager_service.entity.Task;
import com.taskmanager.task_manager_service.repository.ProjectRepository;
import com.taskmanager.task_manager_service.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    
    @GetMapping("/summary")
    public Map<String, Object> getSummary(Principal principal) {
        String username = principal.getName();

        List<Project> projects = projectRepository.findAll(); // optionally filter by user
        List<Task> tasks = taskRepository.findAll();

        long completed = tasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
        long inprogress = tasks.stream().filter(t -> "In PROGRESS".equalsIgnoreCase(t.getStatus())).count();
        long pending = tasks.stream().filter(t -> !"COMPLETED".equalsIgnoreCase(t.getStatus())).count();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalProjects", projects.size());
        summary.put("totalTasks", tasks.size());
        summary.put("completedTasks", completed);
        summary.put("inProgressTasks", inprogress);
        summary.put("pendingTasks", pending);

        return summary;
    }

   
    @GetMapping("/project-stats")
    public List<Map<String, Object>> getProjectStats() {
        List<Project> projects = projectRepository.findAll();

        return projects.stream().map(project -> {
            List<Task> projectTasks = taskRepository.findByProjectId(project.getId());

            long completed = projectTasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
            long inProgress = projectTasks.stream().filter(t -> "In PROGRESS".equalsIgnoreCase(t.getStatus())).count();
            long pending = projectTasks.stream().filter(t -> "New".equalsIgnoreCase(t.getStatus())).count();

            Map<String, Object> map = new HashMap<>();
            map.put("projectId", project.getId());
            map.put("projectName", project.getName());
            map.put("totalTasks", projectTasks.size());
            map.put("completed", completed);
            map.put("inProgress", inProgress);
            map.put("pending", pending);
            return map;
        }).collect(Collectors.toList());
    }

   
    @GetMapping("/my-tasks")
    public List<Map<String, Object>> getMyTasks(Principal principal) {
        String email = principal.getName(); // from JWT
        List<Task> tasks = taskRepository.findByAssignee_Email(email);

        return tasks.stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("taskId", t.getId());
            map.put("title", t.getTitle());
            map.put("status", t.getStatus());
            map.put("projectName", t.getProject().getName());
            return map;
        }).collect(Collectors.toList());
    }

}
