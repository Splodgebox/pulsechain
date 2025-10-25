# 🧩 Pulsechain

**Pulsechain** is a lightweight, modular **task orchestration framework** for Java - designed to schedule, execute, and manage interdependent tasks in an event-driven flow.

It acts as a mini workflow engine, similar in concept to tools like Apache Airflow, but simplified for pure Java environments.

---

## ⚙️ Overview

Pulsechain allows you to:
- Define **tasks** with clear dependencies (`A → B → C`).
- Automatically manage **execution order** based on those dependencies.
- Execute tasks **synchronously or asynchronously** using configurable schedulers.
- Register and store tasks using a clean **registry system**.
- Add **retries, error handling, and state tracking** out of the box.

Each task in Pulsechain is self-contained - it defines what it does and what it depends on.
The scheduler then analyzes those dependencies and executes tasks in the correct order, while respecting parallelism and failure conditions.

---

## Future Goals

- Add JSON-based task configuration and DAG visualization.
- Introduce delayed/scheduled task execution.
- Support for distributed execution via socket or REST workers.
- CLI dashboard to monitor live task states.

---

## License
This project is open-source and free to use for educational or production purposes.
