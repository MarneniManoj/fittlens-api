package com.fittlens.core.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String name;
    private String deviceId;
    private String email;
}