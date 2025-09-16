package com.github.oleksandrkukotin.jowl.indexing.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndexOperationResponse {
    
    private final boolean success;
    private final String message;
    private final String operation;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;
    
    private final Long durationMs;
    private final Map<String, Object> details;

    public IndexOperationResponse(boolean success, String message, String operation) {
        this.success = success;
        this.message = message;
        this.operation = operation;
        this.timestamp = LocalDateTime.now();
        this.durationMs = null;
        this.details = null;
    }

    public IndexOperationResponse(boolean success, String message, String operation, Long durationMs) {
        this.success = success;
        this.message = message;
        this.operation = operation;
        this.timestamp = LocalDateTime.now();
        this.durationMs = durationMs;
        this.details = null;
    }

    public IndexOperationResponse(boolean success, String message, String operation, Long durationMs, Map<String, Object> details) {
        this.success = success;
        this.message = message;
        this.operation = operation;
        this.timestamp = LocalDateTime.now();
        this.durationMs = durationMs;
        this.details = details;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getOperation() {
        return operation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}

