package com.taskmanager.task_manager_service.controller;

import com.taskmanager.task_manager_service.entity.Attachement;
import com.taskmanager.task_manager_service.repository.AttachementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attachments")
public class AttachementController {

    @Autowired
    private AttachementRepository attachmentRepository;

    @PostMapping
    public ResponseEntity<Attachement> uploadAttachment(@RequestBody Attachement attachment) {
        attachment.setUploadedAt(LocalDateTime.now());
        return ResponseEntity.ok(attachmentRepository.save(attachment));
    }

    @GetMapping
    public List<Attachement> getAllAttachments() {
        return attachmentRepository.findAll();
    }

    @GetMapping("/task/{taskId}")
    public List<Attachement> getAttachmentsByTask(@PathVariable Long taskId) {
        return attachmentRepository.findAll().stream()
                .filter(a -> a.getTask().getId().equals(taskId))
                .toList();
    }
}
