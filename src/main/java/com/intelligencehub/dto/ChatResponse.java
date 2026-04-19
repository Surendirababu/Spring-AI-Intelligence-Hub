package com.intelligencehub.dto;

import java.time.LocalDateTime;

public record ChatResponse(
        boolean success,
        String response,
        LocalDateTime timestamp,
        String errorMessage) {
    public static ChatResponse success(String response, LocalDateTime timestamp) {
        return new ChatResponse(true, response, timestamp, null);
    }

    public static ChatResponse error(String errorMessage) {
        return new ChatResponse(false, null, LocalDateTime.now(), errorMessage);
    }
}