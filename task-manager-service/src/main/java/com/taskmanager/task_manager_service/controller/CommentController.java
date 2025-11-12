package com.taskmanager.task_manager_service.controller;


import com.taskmanager.task_manager_service.entity.Comment;
import com.taskmanager.task_manager_service.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(commentRepository.save(comment));
    }

    @GetMapping
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @GetMapping("/task/{taskId}")
    public List<Comment> getCommentsByTask(@PathVariable Long taskId) {
        return commentRepository.findAll().stream()
                .filter(c -> c.getTask().getId().equals(taskId))
                .toList();
    }
}
  


