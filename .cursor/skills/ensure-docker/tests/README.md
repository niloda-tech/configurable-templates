# ensure-docker tests

Tests for the ensure-docker skill: the script is expected to **detect Docker when it is running** and **start Colima when it is not**.

## Run tests

From the **project root**:

```bash
bash .cursor/skills/ensure-docker/tests/run_tests.sh
```

Or, if you have [bats-core](https://github.com/bats-core/bats-core) installed (`brew install bats-core`):

```bash
bats .cursor/skills/ensure-docker/tests/ensure-docker.bats
```

## Test cases

| Test | Description |
|------|-------------|
| **Docker already running (default)** | `docker info` succeeds with default connection → script exits 0 and prints "Docker is already running." |
| **Docker already running (Colima socket)** | Default `docker info` fails but Colima socket exists and `docker info` with `DOCKER_HOST` set succeeds → script exits 0 and prints "Docker is already running (Colima)." (Skipped if a Unix socket cannot be created, e.g. in some CI/sandbox environments.) |
| **Colima not installed** | Docker not reachable and `colima` not in PATH → script exits 1 and prints "Colima is not installed". |
| **Starts Colima and Docker becomes ready** | Docker not reachable, Colima is in PATH, no Colima socket → script starts Colima and waits until `docker info` succeeds, then exits 0 with "Docker is ready." |

Tests use **mocks** for `docker`, `colima`, `nohup`, and `sleep` so no real Docker or Colima is started; see `tests/mocks/` and `tests/mocks-no-colima/`.
