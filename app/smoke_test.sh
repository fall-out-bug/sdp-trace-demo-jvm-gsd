#!/bin/bash
set -e

PORT=8080

# In Bazel test context, use runfiles to locate the app
if [[ -n "$TEST_SRCDIR" ]]; then
    APP_BINARY="$TEST_SRCDIR/_main/app/app"
else
    # Fallback for local execution
    APP_BINARY="$(pwd)/../bazel-bin/app/app"
fi

# Start the app in background
"$APP_BINARY" &
APP_PID=$!

cleanup() {
    kill $APP_PID 2>/dev/null || true
}
trap cleanup EXIT

# Wait for server to be ready (max 60s)
echo "Waiting for server to start..."
for i in $(seq 1 60); do
    if curl -s http://localhost:$PORT/health >/dev/null 2>&1; then
        break
    fi
    sleep 1
done

# Call health endpoint and check response
RESPONSE=$(curl -s http://localhost:$PORT/health)
echo "Health response: $RESPONSE"

# Verify response contains "ok"
if echo "$RESPONSE" | grep -q '"status":"ok"'; then
    echo "PASS: Health check returned status ok"
    exit 0
else
    echo "FAIL: Health check did not return status ok"
    exit 1
fi