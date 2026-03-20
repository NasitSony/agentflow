package com.veriprotocol.agentflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veriprotocol.agentflow.model.TaskLog;

public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {
    List<TaskLog> findByTaskId(Long taskId);
}
