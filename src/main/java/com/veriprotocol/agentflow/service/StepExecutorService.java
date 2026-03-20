package com.veriprotocol.agentflow.service;


import org.springframework.stereotype.Service;

@Service
public class StepExecutorService {

   
    public void execute(String stepName, String goal) {
    	
    	if ("send_email".equals(stepName) && Math.random() < 0.3) {
    	    throw new RuntimeException("Simulated failure in send_email");
    	}

        long start = System.currentTimeMillis();

        simulateWork();

        long duration = System.currentTimeMillis() - start;

        if (duration > 1000) {
            throw new RuntimeException("Step timeout");
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
