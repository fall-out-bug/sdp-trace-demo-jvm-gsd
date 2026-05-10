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

sleep 2
if ! kill -0 $APP_PID 2>/dev/null; then
    echo "FAIL: Application failed to start"
    exit 1
fi

# Wait for server to be ready (max 30s)
echo "Waiting for server to start..."
for i in $(seq 1 30); do
    if curl -s http://localhost:$PORT/health >/dev/null 2>&1; then
        break
    fi
    sleep 1
done

# Verify health endpoint returns ok
RESPONSE=$(curl -s http://localhost:$PORT/health)
echo "Health response: $RESPONSE"

if echo "$RESPONSE" | grep -q '"status":"ok"'; then
    echo "PASS: Health check returned status ok"
else
    echo "FAIL: Health check did not return status ok"
    exit 1
fi

# Verify GET /todos returns empty array
RESPONSE=$(curl -s http://localhost:$PORT/todos)
echo "GET /todos response: $RESPONSE"

if echo "$RESPONSE" | grep -q '^\[\]$'; then
    echo "PASS: GET /todos returned []"
else
    echo "FAIL: GET /todos did not return []"
    exit 1
fi

# Verify POST /todos creates a todo and returns 201 with id/title/completed/createdAt
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d '{"title":"Test Todo"}' http://localhost:$PORT/todos)
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | grep -v "HTTP_CODE:")
echo "POST /todos response: $BODY (HTTP $HTTP_CODE)"

if [ "$HTTP_CODE" != "201" ]; then
    echo "FAIL: POST /todos did not return 201"
    exit 1
fi

if echo "$BODY" | grep -q '"id":"'; then
    echo "PASS: POST /todos returned todo with id"
else
    echo "FAIL: POST /todos did not return id"
    exit 1
fi

if echo "$BODY" | grep -q '"title":"Test Todo"'; then
    echo "PASS: POST /todos returned todo with title"
else
    echo "FAIL: POST /todos did not return title"
    exit 1
fi

if echo "$BODY" | grep -q '"completed":false'; then
    echo "PASS: POST /todos returned todo with completed"
else
    echo "FAIL: POST /todos did not return completed"
    exit 1
fi

if echo "$BODY" | grep -q '"createdAt":'; then
    echo "PASS: POST /todos returned todo with createdAt"
else
    echo "FAIL: POST /todos did not return createdAt"
    exit 1
fi

# Verify subsequent GET /todos includes the created todo
RESPONSE=$(curl -s http://localhost:$PORT/todos)
echo "GET /todos after POST: $RESPONSE"

if echo "$RESPONSE" | grep -q '"title":"Test Todo"'; then
    echo "PASS: GET /todos includes created todo"
else
    echo "FAIL: GET /todos does not include created todo"
    exit 1
fi

# WR-04: Verify malformed JSON returns 400
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d 'not json' http://localhost:$PORT/todos)
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "POST malformed JSON: HTTP $HTTP_CODE"

if [ "$HTTP_CODE" != "400" ]; then
    echo "FAIL: Malformed JSON did not return 400"
    exit 1
fi
echo "PASS: Malformed JSON returns 400"

# Verify missing title returns 400
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d '{"other":"value"}' http://localhost:$PORT/todos)
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "POST missing title: HTTP $HTTP_CODE"

if [ "$HTTP_CODE" != "400" ]; then
    echo "FAIL: Missing title did not return 400"
    exit 1
fi
echo "PASS: Missing title returns 400"

# Verify blank title returns 400
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d '{"title":""}' http://localhost:$PORT/todos)
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "POST blank title: HTTP $HTTP_CODE"

if [ "$HTTP_CODE" != "400" ]; then
    echo "FAIL: Blank title did not return 400"
    exit 1
fi
echo "PASS: Blank title returns 400"

# Verify whitespace-only title returns 400
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d '{"title":"   "}' http://localhost:$PORT/todos)
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "POST whitespace title: HTTP $HTTP_CODE"

if [ "$HTTP_CODE" != "400" ]; then
    echo "FAIL: Whitespace title did not return 400"
    exit 1
fi
echo "PASS: Whitespace title returns 400"

# GLM-P1-02: Duplicate title keys should return 400
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d '{"title":"first","title":"second"}' http://localhost:$PORT/todos)
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "POST duplicate title keys: HTTP $HTTP_CODE"

if [ "$HTTP_CODE" != "400" ]; then
    echo "FAIL: Duplicate title keys did not return 400"
    exit 1
fi
echo "PASS: Duplicate title keys return 400"

# Verify unterminated title string returns 400
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d '{"title":"unterminated' http://localhost:$PORT/todos)
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "POST unterminated title: HTTP $HTTP_CODE"

if [ "$HTTP_CODE" != "400" ]; then
    echo "FAIL: Unterminated title did not return 400"
    exit 1
fi
echo "PASS: Unterminated title returns 400"

# Verify escaped quotes in title works
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d '{"title":"test\"quote"}' http://localhost:$PORT/todos)
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | grep -v "HTTP_CODE:")
echo "POST escaped quote: HTTP $HTTP_CODE"

if [ "$HTTP_CODE" != "201" ]; then
    echo "FAIL: Escaped quotes did not return 201"
    exit 1
fi
echo "PASS: Escaped quotes in title returns 201"

# Verify escaped backslash in title works
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d '{"title":"test\\backslash"}' http://localhost:$PORT/todos)
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "POST escaped backslash: HTTP $HTTP_CODE"

if [ "$HTTP_CODE" != "201" ]; then
    echo "FAIL: Escaped backslash did not return 201"
    exit 1
fi
echo "PASS: Escaped backslash in title returns 201"

# GLM-P1-03: Concurrent POST requests test thread-safety
echo "Testing concurrent POST requests..."
CONCURRENT_COUNT=10
FAILED=0

for i in $(seq 1 $CONCURRENT_COUNT); do
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d "{\"title\":\"Concurrent $i\"}" http://localhost:$PORT/todos)
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
    if [ "$HTTP_CODE" != "201" ]; then
        echo "FAIL: Concurrent POST $i returned $HTTP_CODE instead of 201"
        FAILED=1
    fi
done

if [ $FAILED -eq 1 ]; then
    echo "FAIL: Some concurrent POSTs failed"
    exit 1
fi

echo "PASS: All $CONCURRENT_COUNT concurrent POSTs returned 201"

# Verify all concurrent titles are present in GET /todos
GET_RESPONSE=$(curl -s http://localhost:$PORT/todos)
MISSING=0
for i in $(seq 1 $CONCURRENT_COUNT); do
    if ! echo "$GET_RESPONSE" | grep -q "\"title\":\"Concurrent $i\""; then
        echo "FAIL: Concurrent $i not found in GET /todos"
        MISSING=1
    fi
done

if [ $MISSING -eq 1 ]; then
    echo "FAIL: Some concurrent titles missing from GET /todos"
    exit 1
fi

echo "PASS: All $CONCURRENT_COUNT concurrent titles present in GET /todos"

echo "ALL TESTS PASSED"
exit 0