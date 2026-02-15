---
name: ensure-docker
description: Ensure Docker is running; if not, start Colima in the background. Use before cot-sandbox tests, sandbox execution, or any task that needs Docker.
compatibility: macOS with Colima; Docker and Colima installed (e.g. brew install docker colima).
---

# Ensure Docker

Checks if Docker is reachable; if not, starts Colima in the background and waits until Docker is ready. Use before any Docker-dependent work (e.g. cot-sandbox module).

## When to Use

- Before running cot-sandbox integration tests or sandbox execution
- When the user mentions Docker not running, Colima, or sandbox failures
- Before any task that requires the Docker daemon

## Instructions

1. **Run the ensure-docker script** from the **project root**:
   ```bash
   bash .cursor/skills/ensure-docker/scripts/ensure-docker.sh
   ```
   The script checks if Docker is reachable; if not, it starts Colima in the background and waits until Docker is ready (or times out after 120s; override with `DOCKER_READY_MAX_WAIT`).

2. **After Docker is ready**, continue with the user's task (e.g. run tests, build the cot-sandbox image).

Always run the script first when the task involves Docker; do not assume Docker is already running.
