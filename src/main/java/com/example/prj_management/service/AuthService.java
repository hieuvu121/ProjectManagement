package com.example.prj_management.service;

import com.example.prj_management.dto.request.LoginRequest;
import com.example.prj_management.dto.response.LoginResponse;
import com.example.prj_management.dto.request.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout(String token);
}
