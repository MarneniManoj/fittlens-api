package com.fittlens.core.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private String message;
    private String code;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }
} 