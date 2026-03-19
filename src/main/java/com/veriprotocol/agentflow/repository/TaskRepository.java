package com.veriprotocol.agentflow.repository;


import com.veriprotocol.agentflow.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
