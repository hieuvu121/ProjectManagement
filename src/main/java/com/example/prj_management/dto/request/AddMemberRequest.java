package com.example.prj_management.dto.request;

import com.example.prj_management.enums.Role;
import lombok.Data;

@Data
public class AddMemberRequest {
    private Long userId;
    private Role role;
}
