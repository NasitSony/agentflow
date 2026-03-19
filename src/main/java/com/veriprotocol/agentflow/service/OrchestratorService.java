package com.veriprotocol.agentflow.service;


import com.veriprotocol.agentflow.model.Step;
import com.veriprotocol.agentflow.model.StepStatus;
import com.veriprotocol.agentflow.model.Task;
import com.veriprotocol.agentflow.model.TaskStatus;
import com.veriprotocol.agentflow.repository.StepRepository;
import com.veriprotocol.agentflow.repository.TaskRepository;
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

    public OrchestratorService(TaskRepository taskRepository,
                               StepRepository stepRepository,
                               WorkflowPlannerService workflowPlannerService,
                               StepExecutorService stepExecutorService) {
        this.taskRepository = taskRepository;
        this.stepRepository = stepRepository;
        this.workflowPlannerService = workflowPlannerService;
        this.stepExecutorService = stepExecutorService;
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

            step.setStatus(StepStatus.IN_PROGRESS);
            stepRepository.save(step);

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
}
