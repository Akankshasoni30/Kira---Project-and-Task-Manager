package com.taskmanager.task_manager_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private String text;
    private String username;
    private Long taskId;
    private LocalDateTime createdAt;
}
