# Cot-Sandbox and Docker

This module requires **Docker** to be running for integration tests and sandbox execution. Use the project Cursor skill and script so Docker (and Colima, if used) is available before any Docker-dependent work.

## Use the Cursor skill

The project defines a **Cursor skill** that ensures Docker is running and starts Colima in the background if needed:

- **Skill name:** `ensure-docker` (in `.cursor/skills/ensure-docker/`)
- **Invoke in Agent chat:** type `/ensure-docker` to run the skill before running tests or building the sandbox.

The skill runs the ensure-docker script and then you can proceed with your task.

## Ensure Docker before Docker-dependent work

From the **project root** (parent of `cot-sandbox/`), run:

```bash
bash .cursor/skills/ensure-docker/scripts/ensure-docker.sh
```

This checks if Docker is reachable; if not, it starts Colima in the background and waits until Docker is ready. Then, from the project root:

- **Integration tests:** `./gradlew :cot-sandbox:test --tests "*DockerSandbox*"`
- **Build image:** `cd cot-sandbox && docker build -t cot-sandbox:latest .`
- **All tests:** `./gradlew :cot-sandbox:test`

See [README.md](README.md) for full module documentation and [.cursor/skills/ensure-docker/SKILL.md](../.cursor/skills/ensure-docker/SKILL.md) for the full skill definition.
