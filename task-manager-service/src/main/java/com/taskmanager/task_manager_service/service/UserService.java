package com.taskmanager.task_manager_service.service;

import com.taskmanager.task_manager_service.dto.AuthRequest;
import com.taskmanager.task_manager_service.dto.AuthResponse;
import com.taskmanager.task_manager_service.entity.User;
import com.taskmanager.task_manager_service.repository.UserRepository;
import com.taskmanager.task_manager_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent() && passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            String token = jwtUtil.generateToken(user.get().getEmail());
            String email = user.get().getEmail();
            String role = user.get().getRole();
            String name = user.get().getName();
            Long id = user.get().getId();
            return new AuthResponse(token,email,name,role,id);
        }
        throw new RuntimeException("Invalid email or password");
    }
}
