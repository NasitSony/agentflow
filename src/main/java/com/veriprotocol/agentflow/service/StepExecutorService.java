package com.veriprotocol.agentflow.service;


import org.springframework.stereotype.Service;

@Service
public class StepExecutorService {

    public void execute(String stepName, String goal) {
        System.out.println("Executing step: " + stepName + " for goal: " + goal);

        // Mock execution for now
        switch (stepName) {
            case "generate_summary" -> simulateWork();
            case "send_email" -> simulateWork();
            case "analyze_goal" -> simulateWork();
            case "store_result" -> simulateWork();
            default -> throw new IllegalArgumentException("Unknown step: " + stepName);
        }
    }

    private void simulateWork() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Step execution interrupted", e);
        }
    }
}
