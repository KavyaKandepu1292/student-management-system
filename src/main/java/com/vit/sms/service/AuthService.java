package com.vit.sms.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vit.sms.dto.AuthResponse;
import com.vit.sms.dto.LoginRequest;
import com.vit.sms.dto.RegisterRequest;
import com.vit.sms.entity.Role;
import com.vit.sms.entity.Student;
import com.vit.sms.entity.User;
import com.vit.sms.exception.DuplicateResourceException;
import com.vit.sms.repository.StudentRepository;
import com.vit.sms.repository.UserRepository;
import com.vit.sms.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setEnabled(true);

        Student linkedStudent = null;

        if (request.getRole() == Role.STUDENT) {

            if (request.getFirstName() == null ||
                request.getLastName() == null ||
                request.getDepartment() == null ||
                request.getGpa() == null) {

                throw new IllegalArgumentException(
                        "firstName, lastName, department and gpa are required to register a student");
            }

            linkedStudent = Student.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .department(request.getDepartment())
                    .gpa(request.getGpa())
                    .build();

            linkedStudent = studentRepository.save(linkedStudent);

            user.setStudent(linkedStudent);
        }

        user = userRepository.save(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getUsername());

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        if (linkedStudent != null) {
            claims.put("studentId", linkedStudent.getId());
        }

        String token = jwtUtil.generateToken(userDetails, claims);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .studentId(linkedStudent != null ? linkedStudent.getId() : null)
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new IllegalStateException("User vanished after authentication"));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getUsername());

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        if (user.getStudent() != null) {
            claims.put("studentId", user.getStudent().getId());
        }

        String token = jwtUtil.generateToken(userDetails, claims);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .studentId(user.getStudent() != null
                        ? user.getStudent().getId()
                        : null)
                .build();
    }
}