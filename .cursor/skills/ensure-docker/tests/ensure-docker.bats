#!/usr/bin/env bats
# Tests for ensure-docker script: detects Docker when running, starts Colima when not.
# Run from project root: bats .cursor/skills/ensure-docker/tests/ensure-docker.bats
# Requires: bats-core (brew install bats-core)

setup() {
  # Resolve paths: tests/ensure-docker.bats -> skill dir -> project root
  TEST_DIR="$(cd "$(dirname "$BATS_TEST_FILENAME")" && pwd)"
  SKILL_DIR="$(cd "$TEST_DIR/.." && pwd)"
  PROJECT_ROOT="$(cd "$SKILL_DIR/../.." && pwd)"
  SCRIPT_PATH="$SKILL_DIR/scripts/ensure-docker.sh"
  MOCK_BIN="$TEST_DIR/mocks"
  export PATH="$MOCK_BIN:$PATH"
}

# --- Detects Docker running (default connection) ---
@test "exits 0 and says Docker is already running when docker info succeeds (default)" {
  export MOCK_DOCKER_DEFAULT=0
  export MOCK_DOCKER_WITH_HOST=0
  run env -i HOME="$HOME" PATH="$MOCK_BIN:$PATH" MOCK_DOCKER_DEFAULT=0 MOCK_DOCKER_WITH_HOST=0 \
    bash "$SCRIPT_PATH"
  [ "$status" -eq 0 ]
  [[ "$output" == *"Docker is already running."* ]]
}

# --- Detects Docker running via Colima socket ---
@test "exits 0 and says Docker is already running when Colima socket exists and docker with DOCKER_HOST succeeds" {
  # Create a real Unix socket so [ -S "$COLIMA_SOCKET" ] passes
  tmpdir="$BATS_TEST_TMPDIR/colima-home"
  mkdir -p "$tmpdir/.colima/default"
  # Create socket (Python is available on macOS)
  python3 -c "
import socket
s = socket.socket(socket.AF_UNIX)
s.bind('$tmpdir/.colima/default/docker.sock')
import time
while True: time.sleep(60)
" &
  SOCK_PID=$!
  # Allow socket to appear
  sleep 0.2
  [ -S "$tmpdir/.colima/default/docker.sock" ]
  # Default docker fails (no DOCKER_HOST), but with DOCKER_HOST set it succeeds
  export MOCK_DOCKER_DEFAULT=1
  export MOCK_DOCKER_WITH_HOST=0
  export HOME="$tmpdir"
  run env HOME="$tmpdir" PATH="$MOCK_BIN:$PATH" MOCK_DOCKER_DEFAULT=1 MOCK_DOCKER_WITH_HOST=0 \
    bash "$SCRIPT_PATH"
  kill $SOCK_PID 2>/dev/null || true
  [ "$status" -eq 0 ]
  [[ "$output" == *"Docker is already running (Colima)."* ]]
}

# --- Docker not running: Colima not installed ---
@test "exits 1 and says Colima is not installed when docker fails and colima not in PATH" {
  # PATH with mock docker but no colima (mocks-no-colima has only docker)
  MOCK_ONLY_DOCKER="$TEST_DIR/mocks-no-colima"
  export MOCK_DOCKER_DEFAULT=1
  export MOCK_DOCKER_WITH_HOST=1
  run env -i HOME="$HOME" PATH="$MOCK_ONLY_DOCKER:$PATH" MOCK_DOCKER_DEFAULT=1 MOCK_DOCKER_WITH_HOST=1 \
    bash "$SCRIPT_PATH"
  [ "$status" -eq 1 ]
  [[ "$output" == *"Colima is not installed"* ]]
}

# --- Docker not running: starts Colima and waits until Docker is ready ---
@test "starts Colima and exits 0 when Docker becomes ready within timeout" {
  # No Colima socket; docker fails at first then succeeds after a few calls
  tmpdir="$BATS_TEST_TMPDIR/ensure-docker-start"
  mkdir -p "$tmpdir"
  counter_file="$tmpdir/docker_calls"
  echo 0 > "$counter_file"
  export MOCK_DOCKER_DEFAULT=1
  export MOCK_DOCKER_WITH_HOST=1
  export MOCK_DOCKER_COUNTER_FILE="$counter_file"
  export MOCK_DOCKER_SUCCESS_AFTER=3
  export DOCKER_READY_MAX_WAIT=10
  run env HOME="$HOME" PATH="$MOCK_BIN:$PATH" \
    MOCK_DOCKER_DEFAULT=1 MOCK_DOCKER_WITH_HOST=1 \
    MOCK_DOCKER_COUNTER_FILE="$counter_file" MOCK_DOCKER_SUCCESS_AFTER=3 \
    DOCKER_READY_MAX_WAIT=10 \
    bash "$SCRIPT_PATH"
  [ "$status" -eq 0 ]
  [[ "$output" == *"Docker is not running"* ]]
  [[ "$output" == *"Starting Colima"* ]]
  [[ "$output" == *"Docker is ready."* ]]
}
