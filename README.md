# AgentFlow — Failure-Aware Workflow Orchestrator

AgentFlow is a production-style backend system that executes multi-step workflows with explicit state transitions, failure handling, and deterministic recovery.

It is designed to model **reliable execution of agent-like workflows**, where each step may fail, retry, or require escalation.

---

## 🎯 Why This Project

Most AI/agent systems focus on generating outputs.

This project focuses on something harder:

> **What happens when execution fails?**

AgentFlow treats workflows as **distributed systems problems**, not prompt engineering problems.

---

## 🧠 Core Concepts

- **Explicit State Machine**
  - `PENDING → PLANNING → EXECUTING → COMPLETED | FAILED`
- **Step-Based Execution**
  - Ordered steps with independent state
- **Failure-Aware Design**
  - Retry with backoff
  - Timeout handling
  - Failure escalation
- **Deterministic Recovery**
  - All state persisted in database
- **Execution Observability**
  - Step-level logs for debugging

---

## 🏗️ Architecture

```bash
Client
│
▼
REST API (Spring Boot)
│
▼
Orchestrator (State Machine)
│
├── Workflow Planner
├── Step Executor
├── Retry / Timeout Engine
│
▼
PostgreSQL (tasks, steps, logs)
```

---

## ⚙️ Features

### ✅ Workflow Orchestration
- Task submission via API
- Automatic step planning
- Ordered execution

### ✅ State Management
- Task-level states
- Step-level states
- Current step tracking

### ✅ Failure Handling
- Retry with exponential backoff
- Simulated failure injection
- Timeout detection

### ✅ Observability
- Execution logs per step
- Failure reason tracking

---

## 📦 API

### Create Task

```http
POST /tasks
```

### Get Steps

```http
GET /tasks/{id}/steps
```


## 🧪 Example Workflow

Input:

```JSON
{
  "goal": "generate summary and send email"
}
```

Execution:

```bash
PLANNING → [generate_summary, send_email]

EXECUTING:
1. generate_summary → COMPLETED
2. send_email → FAILED → RETRY → COMPLETED
```


## 🔥 Failure Modes Handled

```bash
- Step execution failure
- Retry exhaustion
- Timeout conditions
- Partial workflow completion
```



## 🛠️ Tech Stack

```bash
- Java + Spring Boot
- PostgreSQL
- JPA (Hibernate)
- Docker (for DB)
```


## 🚀 How to Run

### 1. Start PostgreSQL (Docker)

```bash
docker run --name agentflow-postgres \
  -e POSTGRES_DB=agentflow \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5440:5432 \
  -d postgres
```

### 2. Configure application.yml

```YAML
spring:
  datasource:
    url: jdbc:postgresql://127.0.0.1:5440/agentflow
    username: postgres
    password: postgres
```


### 3. Run Application

```bash
./mvnw spring-boot:run
```


### 4. Test
```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"goal":"generate summary and send email"}'
```



## 🧠 Key Design Principles


- **Correctness over convenience**
- **Explicit state over hidden logic**
- **Failure is expected, not exceptional**
- **Deterministic recovery over best-effort execution**
```



## 📌 Future Work

- Async execution (queue-based workers)
- Distributed scheduling
- LLM-based dynamic planning
- Human-in-the-loop escalation



## 💬 Author

Built as part of a distributed systems and AI infrastructure exploration.
