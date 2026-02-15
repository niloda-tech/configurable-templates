#!/usr/bin/env bash
# Ensures Docker is running. If not, starts Colima in the background
# and waits until Docker is available or times out.
# Usage: from project root, run: .cursor/skills/ensure-docker/scripts/ensure-docker.sh

set -e

DOCKER_READY_MAX_WAIT="${DOCKER_READY_MAX_WAIT:-120}"

# Colima uses a socket outside the default path; when this script runs in a minimal
# env (e.g. from Cursor), DOCKER_HOST may be unset so "docker info" fails even
# when Colima is running. Check default first, then Colima's socket.
docker_info_ok() {
  local host="${1:-}"
  if [ -n "$host" ]; then
    DOCKER_HOST="$host" docker info >/dev/null 2>&1
  else
    docker info >/dev/null 2>&1
  fi
}

# Try default connection (respects existing DOCKER_HOST or docker context)
if docker_info_ok; then
  echo "Docker is already running."
  exit 0
fi

# Try Colima's default socket (v0.4.0+); script may run without DOCKER_HOST set
COLIMA_SOCKET="${HOME}/.colima/default/docker.sock"
if [ -S "$COLIMA_SOCKET" ] && docker_info_ok "unix://$COLIMA_SOCKET"; then
  echo "Docker is already running (Colima)."
  exit 0
fi

# Try older Colima socket path (v0.3.4 and older)
COLIMA_OLD_SOCKET="${HOME}/.colima/docker.sock"
if [ -S "$COLIMA_OLD_SOCKET" ] && docker_info_ok "unix://$COLIMA_OLD_SOCKET"; then
  echo "Docker is already running (Colima)."
  exit 0
fi

if ! command -v colima &>/dev/null; then
  echo "Colima is not installed. Install with: brew install colima" >&2
  exit 1
fi

echo "Docker is not running. Starting Colima in the background..."
nohup colima start >/tmp/colima-start.log 2>&1 &
COLIMA_PID=$!

echo "Waiting for Docker to become ready (max ${DOCKER_READY_MAX_WAIT}s)..."
elapsed=0
# Colima exposes its socket at COLIMA_SOCKET; check that path since we may not have DOCKER_HOST set
while [ "$elapsed" -lt "$DOCKER_READY_MAX_WAIT" ]; do
  if docker_info_ok "unix://$COLIMA_SOCKET"; then
    echo "Docker is ready."
    exit 0
  fi
  sleep 2
  elapsed=$((elapsed + 2))
done

echo "Timeout waiting for Docker. Colima may still be starting (PID $COLIMA_PID). Check: tail -f /tmp/colima-start.log" >&2
exit 1
