package com.veriprotocol.agentflow.service;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowPlannerService {

    public List<String> planSteps(String goal) {
        String normalized = goal.toLowerCase();

        if (normalized.contains("email")) {
            return List.of("generate_summary", "send_email");
        }

        if (normalized.contains("summary")) {
            return List.of("generate_summary");
        }

        return List.of("analyze_goal", "store_result");
    }
}
