package com.example.prj_management.service;

import com.example.prj_management.dto.LoginRequest;
import com.example.prj_management.dto.LoginResponse;
import com.example.prj_management.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout(String token);
}
