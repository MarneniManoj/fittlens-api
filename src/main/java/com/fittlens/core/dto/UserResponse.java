package com.fittlens.core.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String uuid;
    private String name;
    private String deviceId;
    private String email;
    private String token;
} 