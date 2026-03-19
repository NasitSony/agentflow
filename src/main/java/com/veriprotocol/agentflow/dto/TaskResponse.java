package com.veriprotocol.agentflow.dto;

import com.veriprotocol.agentflow.model.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskResponse {
    private Long id;
    private String goal;
    private TaskStatus status;
    private String currentStep;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
