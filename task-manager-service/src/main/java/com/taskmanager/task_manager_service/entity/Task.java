package com.taskmanager.task_manager_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status;  
    private String priority; 
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnoreProperties({"tasks"})
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    @JsonIgnoreProperties({"tasks", "comments"}) 
    private User assignee;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"task"}) 
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"task"})
    private List<Attachement> attachements = new ArrayList<>();
}
