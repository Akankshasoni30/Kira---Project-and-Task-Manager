package com.taskmanager.task_manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTaskAssignedEmail(String toEmail, String assigneeName, String taskTitle, String projectName) {
        String subject = "New Task Assigned: " + taskTitle;
        String body = String.format(
            "Hello %s,\n\nYou have been assigned a new task:\n\n" +
            "📋 Task: %s\n🏗 Project: %s\n\nPlease log in to the system to view details.\n\nRegards,\nKira Task Manager",
            assigneeName, taskTitle, projectName
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
