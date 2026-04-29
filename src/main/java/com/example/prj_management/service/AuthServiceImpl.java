package com.example.prj_management.service;

import com.example.prj_management.dto.request.LoginRequest;
import com.example.prj_management.dto.response.LoginResponse;
import com.example.prj_management.dto.request.RegisterRequest;
import com.example.prj_management.entity.TokenBlacklist;
import com.example.prj_management.entity.User;
import com.example.prj_management.repository.TokenBlacklistRepository;
import com.example.prj_management.repository.UserRepository;
import com.example.prj_management.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    public void register(RegisterRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new IllegalArgumentException("user already registered");
        }

        User user=User.builder()
                .email(request.getEmail())
                .name(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token =jwtUtil.generateToken(request.getEmail());
        Date expirationDate = jwtUtil.getExpiration(token);
        LocalDateTime expires = expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return LoginResponse.builder()
                .expiresAt(expires)
                .token(token)
                .build();
    }

    @Override
    public void logout(String token) {
        LocalDateTime expired=jwtUtil.getExpiration(token).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String email=jwtUtil.extractEmail(token);
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("no user found"));
        tokenBlacklistRepository.save(
                TokenBlacklist.builder()
                        .expiresAt(expired)
                        .revokedAt(LocalDateTime.now())
                        .user(user)
                        .token(token)
                        .build()
        );
    }
}
