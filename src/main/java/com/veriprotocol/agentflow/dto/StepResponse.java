package com.veriprotocol.agentflow.dto;

import com.veriprotocol.agentflow.model.StepStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StepResponse {
    private Long id;
    private String name;
    private StepStatus status;
    private Integer retryCount;
    private Integer stepOrder;
}
