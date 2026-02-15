#!/usr/bin/env bash
# Run ensure-docker tests. Uses bats if available, otherwise runs a minimal bash runner.
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"
cd "$PROJECT_ROOT"

if command -v bats &>/dev/null; then
  exec bats "$SCRIPT_DIR/ensure-docker.bats"
fi

# Minimal runner when bats is not installed
MOCK_BIN="$SCRIPT_DIR/mocks"
MOCK_NO_COLIMA="$SCRIPT_DIR/mocks-no-colima"
SCRIPT_PATH="$SCRIPT_DIR/../scripts/ensure-docker.sh"
failed=0

# 1. Docker already running (default)
set +e
out=$(PATH="$MOCK_BIN:$PATH" MOCK_DOCKER_DEFAULT=0 MOCK_DOCKER_WITH_HOST=0 bash "$SCRIPT_PATH" 2>&1)
status=$?
set -e
if [[ $status -eq 0 && "$out" == *"Docker is already running."* ]]; then
  echo "PASS: Docker already running (default)"
else
  echo "FAIL: Docker already running (default) (status=$status)"
  ((failed++)) || true
fi

# 2. Colima socket exists and docker with DOCKER_HOST succeeds (requires Python and socket creation)
tmpdir2="${TMPDIR:-/tmp}/ensure-docker-test2-$$"
mkdir -p "$tmpdir2/.colima/default"
if command -v python3 &>/dev/null; then
  python3 -c "
import socket
s = socket.socket(socket.AF_UNIX)
s.bind('$tmpdir2/.colima/default/docker.sock')
import time
while True: time.sleep(60)
" 2>/dev/null &
  SOCK_PID=$!
  sleep 0.3
  if [[ -S "$tmpdir2/.colima/default/docker.sock" ]]; then
    set +e
    out=$(HOME="$tmpdir2" PATH="$MOCK_BIN:$PATH" MOCK_DOCKER_DEFAULT=1 MOCK_DOCKER_WITH_HOST=0 bash "$SCRIPT_PATH" 2>&1)
    status=$?
    set -e
    if [[ $status -eq 0 && "$out" == *"Docker is already running (Colima)."* ]]; then
      echo "PASS: Docker already running (Colima socket)"
    else
      echo "FAIL: Docker already running (Colima socket) (status=$status)"
      ((failed++)) || true
    fi
  else
    echo "SKIP: Docker already running (Colima socket) (could not create socket)"
  fi
  kill $SOCK_PID 2>/dev/null || true
  wait $SOCK_PID 2>/dev/null || true
  rm -rf "$tmpdir2"
else
  echo "SKIP: Docker already running (Colima socket) (no python3)"
fi

# 3. Colima not installed (PATH has mock docker but no colima)
set +e
out=$(env -i HOME="$HOME" PATH="$MOCK_NO_COLIMA:/usr/bin:/bin" MOCK_DOCKER_DEFAULT=1 MOCK_DOCKER_WITH_HOST=1 bash "$SCRIPT_PATH" 2>&1)
status=$?
set -e
if [[ $status -eq 1 && "$out" == *"Colima is not installed"* ]]; then
  echo "PASS: Colima not installed"
else
  echo "FAIL: Colima not installed (status=$status)"
  ((failed++)) || true
fi

# 4. Starts Colima and Docker becomes ready (use fake HOME so no real Colima socket is found)
tmpdir4="${TMPDIR:-/tmp}/ensure-docker-test4-$$"
mkdir -p "$tmpdir4"
counter_file="$tmpdir4/docker_calls"
echo 0 > "$counter_file"
set +e
out=$(HOME="$tmpdir4" PATH="$MOCK_BIN:$PATH" \
  MOCK_DOCKER_DEFAULT=1 MOCK_DOCKER_WITH_HOST=1 \
  MOCK_DOCKER_COUNTER_FILE="$counter_file" MOCK_DOCKER_SUCCESS_AFTER=3 \
  DOCKER_READY_MAX_WAIT=10 \
  bash "$SCRIPT_PATH" 2>&1)
status=$?
set -e
if [[ $status -eq 0 && "$out" == *"Docker is not running"* && "$out" == *"Docker is ready."* ]]; then
  echo "PASS: Starts Colima and Docker becomes ready"
else
  echo "FAIL: Starts Colima and Docker becomes ready (status=$status)"
  ((failed++)) || true
fi
rm -rf "$tmpdir4"

[[ $failed -eq 0 ]] || exit 1
