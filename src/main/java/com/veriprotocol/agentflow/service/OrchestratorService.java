package com.veriprotocol.agentflow.service;


import com.veriprotocol.agentflow.model.Step;
import com.veriprotocol.agentflow.model.StepStatus;
import com.veriprotocol.agentflow.model.Task;
import com.veriprotocol.agentflow.model.TaskLog;
import com.veriprotocol.agentflow.model.TaskStatus;
import com.veriprotocol.agentflow.repository.StepRepository;
import com.veriprotocol.agentflow.repository.TaskRepository;
import com.veriprotocol.agentflow.repository.TaskLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrchestratorService {

    private final TaskRepository taskRepository;
    private final StepRepository stepRepository;
    private final WorkflowPlannerService workflowPlannerService;
    private final StepExecutorService stepExecutorService;
    private final TaskLogRepository taskLogRepository;

    public OrchestratorService(TaskRepository taskRepository,
                               StepRepository stepRepository,
                               WorkflowPlannerService workflowPlannerService,
                               StepExecutorService stepExecutorService, TaskLogRepository taskLogRepository) {
        this.taskRepository = taskRepository;
        this.stepRepository = stepRepository;
        this.workflowPlannerService = workflowPlannerService;
        this.stepExecutorService = stepExecutorService;
        this.taskLogRepository = taskLogRepository;
    }

    @Transactional
    public void processTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        task.setStatus(TaskStatus.PLANNING);
        taskRepository.save(task);

        List<String> plannedSteps = workflowPlannerService.planSteps(task.getGoal());
        List<Step> savedSteps = createSteps(task.getId(), plannedSteps);

        task.setStatus(TaskStatus.EXECUTING);
        taskRepository.save(task);
        
        
       

        for (Step step : savedSteps) {
            
            
            task.setCurrentStep(step.getName());
            taskRepository.save(task);

            boolean success = executeWithRetry(step, task);

            if (!success) {
                task.setStatus(TaskStatus.FAILED);
                taskRepository.save(task);
                return;
            }

            try {
                stepExecutorService.execute(step.getName(), task.getGoal());
                step.setStatus(StepStatus.COMPLETED);
                stepRepository.save(step);
            } catch (Exception e) {
                step.setStatus(StepStatus.FAILED);
                stepRepository.save(step);

                task.setStatus(TaskStatus.FAILED);
                taskRepository.save(task);
                return;
            }
        }

        task.setCurrentStep(null);
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);
    }

    private List<Step> createSteps(Long taskId, List<String> plannedSteps) {
        List<Step> steps = new ArrayList<>();

        for (int i = 0; i < plannedSteps.size(); i++) {
            Step step = new Step();
            step.setTaskId(taskId);
            step.setName(plannedSteps.get(i));
            step.setStatus(StepStatus.PENDING);
            step.setStepOrder(i + 1);
            steps.add(step);
        }

        return stepRepository.saveAll(steps);
    }
    
    private boolean executeWithRetry(Step step, Task task) {
        int maxRetries = 3;

        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {

            step.setStatus(StepStatus.IN_PROGRESS);
            step.setRetryCount(attempt - 1);
            stepRepository.save(step);
            log(task.getId(), step.getName(), "START", "Attempt " + attempt);

            try {
                stepExecutorService.execute(step.getName(), task.getGoal());

                step.setStatus(StepStatus.COMPLETED);
                stepRepository.save(step);
                
                log(task.getId(), step.getName(), "SUCCESS", "Completed");
                
                return true;

            } catch (Exception e) {

                step.setStatus(StepStatus.FAILED);
                step.setRetryCount(attempt);
                stepRepository.save(step);
                

                if (attempt == maxRetries) {
                	log(task.getId(), step.getName(), "FAIL", e.getMessage());
                    return false;
                }

                log(task.getId(), step.getName(), "RETRIES", e.getMessage());
                backoff(attempt);
            }
        }

        return false;
    }
    
    private void backoff(int attempt) {
        try {
            Thread.sleep(500L * attempt); // simple exponential-ish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void log(Long taskId, String step, String status, String message) {
        TaskLog log = new TaskLog();
        log.setTaskId(taskId);
        log.setStepName(step);
        log.setStatus(status);
        log.setMessage(message);
        taskLogRepository.save(log);
    }
}
