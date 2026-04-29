package com.example.prj_management.service;

import com.example.prj_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public com.example.prj_management.entity.User findCurrUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        com.example.prj_management.entity.User currUser = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return currUser;

    }
}

