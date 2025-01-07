package com.musinsa.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private String status;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ApiError(String status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
