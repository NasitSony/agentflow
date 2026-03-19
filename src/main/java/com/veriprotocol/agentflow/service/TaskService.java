package com.veriprotocol.agentflow.service;


import com.veriprotocol.agentflow.dto.CreateTaskRequest;
import com.veriprotocol.agentflow.dto.TaskResponse;
import com.veriprotocol.agentflow.model.Task;
import com.veriprotocol.agentflow.model.TaskStatus;
import com.veriprotocol.agentflow.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setGoal(request.getGoal());
        task.setStatus(TaskStatus.PENDING);

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    public TaskResponse getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));

        return toResponse(task);
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .goal(task.getGoal())
                .status(task.getStatus())
                .currentStep(task.getCurrentStep())
                .retryCount(task.getRetryCount())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
