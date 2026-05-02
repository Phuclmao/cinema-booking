package com.example.cinema.service;

import com.example.cinema.dto.Dto.*;
import com.example.cinema.entity.User;
import com.example.cinema.entity.User.Role;
import com.example.cinema.exception.Exceptions.*;
import com.example.cinema.repository.UserRepository;
import com.example.cinema.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new ResourceAlreadyExistsException("Username '" + request.getUsername() + "' đã tồn tại");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new ResourceAlreadyExistsException("Email '" + request.getEmail() + "' đã được sử dụng");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user.getUsername()))
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Đăng ký thành công!")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User không tìm thấy"));
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user.getUsername()))
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Đăng nhập thành công!")
                .build();
    }
}
