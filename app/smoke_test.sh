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

TEMP_DIR=""
cleanup() {
    kill $APP_PID 2>/dev/null || true
    rm -rf "$TEMP_DIR" 2>/dev/null || true
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

# GLM-P1-03: Concurrent POST requests test thread-safety (real parallel)
echo "Testing parallel concurrent POST requests..."
CONCURRENT_COUNT=10
TEMP_DIR=$(mktemp -d)

run_parallel_post() {
    local num=$1
    local outfile="$TEMP_DIR/out_$num"
    local http_code
    local response
    response=$(curl -s --max-time 30 --connect-timeout 5 -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d "{\"title\":\"Concurrent $num\"}" http://localhost:$PORT/todos)
    http_code=$(echo "$response" | grep "HTTP_CODE:" | cut -d: -f2)
    echo "$http_code" > "$outfile"
}

PIDS=""
for i in $(seq 1 $CONCURRENT_COUNT); do
    run_parallel_post $i &
    PIDS="$PIDS $!"
done

for pid in $PIDS; do
    wait $pid || true
done

FAILED=0
for i in $(seq 1 $CONCURRENT_COUNT); do
    outfile="$TEMP_DIR/out_$i"
    HTTP_CODE=$(cat "$outfile")
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
GET_RESPONSE=$(curl -s --max-time 30 --connect-timeout 5 http://localhost:$PORT/todos)
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

# Verify all IDs are unique (no duplicate IDs from race conditions)
ID_COUNT=$(echo "$GET_RESPONSE" | grep -o '"id":"[^"]*"' | wc -l | tr -d ' ')
UNIQUE_ID_COUNT=$(echo "$GET_RESPONSE" | grep -o '"id":"[^"]*"' | sort -u | wc -l | tr -d ' ')
if [ "$ID_COUNT" != "$UNIQUE_ID_COUNT" ]; then
    echo "FAIL: Duplicate IDs detected ($ID_COUNT total vs $UNIQUE_ID_COUNT unique)"
    exit 1
fi

echo "PASS: All concurrent todo IDs are unique"

# F3-01: Verify POST /todos/{id}/complete marks todo as completed
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d '{"title":"Complete Me"}' http://localhost:$PORT/todos)
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | grep -v "HTTP_CODE:")
echo "POST /todos for complete test: $BODY (HTTP $HTTP_CODE)"

if [ "$HTTP_CODE" != "201" ]; then
    echo "FAIL: POST /todos for complete test did not return 201"
    exit 1
fi

# Extract todo ID from response
TODO_ID=$(echo "$BODY" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Created todo ID: $TODO_ID"

if [ -z "$TODO_ID" ]; then
    echo "FAIL: Could not extract todo ID from POST response"
    exit 1
fi

# F3-02: Verify POST /todos/{id}/complete returns 200 and completed=true
COMPLETE_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST http://localhost:$PORT/todos/$TODO_ID/complete)
COMPLETE_HTTP_CODE=$(echo "$COMPLETE_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
COMPLETE_BODY=$(echo "$COMPLETE_RESPONSE" | grep -v "HTTP_CODE:")
echo "POST /todos/$TODO_ID/complete: $COMPLETE_BODY (HTTP $COMPLETE_HTTP_CODE)"

if [ "$COMPLETE_HTTP_CODE" != "200" ]; then
    echo "FAIL: POST /todos/$TODO_ID/complete did not return 200"
    exit 1
fi

if echo "$COMPLETE_BODY" | grep -q '"completed":true'; then
    echo "PASS: POST /todos/$TODO_ID/complete returned completed=true"
else
    echo "FAIL: POST /todos/$TODO_ID/complete did not return completed=true"
    exit 1
fi

# F3-03: Verify subsequent GET /todos includes the completed todo with completed=true
GET_RESPONSE=$(curl -s http://localhost:$PORT/todos)
echo "GET /todos after complete: $GET_RESPONSE"

if echo "$GET_RESPONSE" | grep -q "\"completed\":true"; then
    echo "PASS: GET /todos shows completed=true"
else
    echo "FAIL: GET /todos does not show completed=true"
    exit 1
fi

# F3-04: Verify POST /todos/{unknown id}/complete returns 404
UNKNOWN_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST http://localhost:$PORT/todos/unknown-id-123/complete)
UNKNOWN_HTTP_CODE=$(echo "$UNKNOWN_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "POST /todos/unknown-id-123/complete: HTTP $UNKNOWN_HTTP_CODE"

if [ "$UNKNOWN_HTTP_CODE" != "404" ]; then
    echo "FAIL: POST /todos/unknown-id-123/complete did not return 404"
    exit 1
fi
echo "PASS: Unknown ID returns 404"

# F3-05: Verify unsupported methods on /todos/{id}/complete return 405
GET_UNSUPPORTED=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET http://localhost:$PORT/todos/$TODO_ID/complete)
GET_HTTP_CODE=$(echo "$GET_UNSUPPORTED" | grep "HTTP_CODE:" | cut -d: -f2)
echo "GET /todos/$TODO_ID/complete: HTTP $GET_HTTP_CODE"

if [ "$GET_HTTP_CODE" != "405" ]; then
    echo "FAIL: GET on /todos/$TODO_ID/complete did not return 405"
    exit 1
fi
echo "PASS: Unsupported method returns 405"

DELETE_UNSUPPORTED=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X DELETE http://localhost:$PORT/todos/$TODO_ID/complete)
DELETE_HTTP_CODE=$(echo "$DELETE_UNSUPPORTED" | grep "HTTP_CODE:" | cut -d: -f2)
echo "DELETE /todos/$TODO_ID/complete: HTTP $DELETE_HTTP_CODE"

if [ "$DELETE_HTTP_CODE" != "405" ]; then
    echo "FAIL: DELETE on /todos/$TODO_ID/complete did not return 405"
    exit 1
fi
echo "PASS: DELETE returns 405"

# F4-01: Verify DELETE /todos/{id} returns 204 for existing todo
DELETE_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X DELETE http://localhost:$PORT/todos/$TODO_ID)
DELETE_HTTP_CODE=$(echo "$DELETE_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "DELETE /todos/$TODO_ID: HTTP $DELETE_HTTP_CODE"

if [ "$DELETE_HTTP_CODE" != "204" ]; then
    echo "FAIL: DELETE /todos/$TODO_ID did not return 204"
    exit 1
fi
echo "PASS: DELETE /todos/$TODO_ID returns 204"

# F4-02: Verify deleted todo does not appear in GET /todos
GET_AFTER_DELETE=$(curl -s http://localhost:$PORT/todos)
echo "GET /todos after delete: $GET_AFTER_DELETE"

if echo "$GET_AFTER_DELETE" | grep -q "\"title\":\"Complete Me\""; then
    echo "FAIL: Deleted todo still appears in GET /todos"
    exit 1
fi
echo "PASS: Deleted todo does not appear in GET /todos"

# F4-03: Verify DELETE /todos/{unknown id} returns 404
UNKNOWN_DELETE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X DELETE http://localhost:$PORT/todos/unknown-id-456)
UNKNOWN_DELETE_CODE=$(echo "$UNKNOWN_DELETE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "DELETE /todos/unknown-id-456: HTTP $UNKNOWN_DELETE_CODE"

if [ "$UNKNOWN_DELETE_CODE" != "404" ]; then
    echo "FAIL: DELETE /todos/unknown-id-456 did not return 404"
    exit 1
fi
echo "PASS: Unknown ID returns 404"

# F4-04: Verify GET /todos/{id} returns 405
GET_ON_ID=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET http://localhost:$PORT/todos/$TODO_ID)
GET_ON_ID_CODE=$(echo "$GET_ON_ID" | grep "HTTP_CODE:" | cut -d: -f2)
echo "GET /todos/$TODO_ID: HTTP $GET_ON_ID_CODE"

if [ "$GET_ON_ID_CODE" != "405" ]; then
    echo "FAIL: GET /todos/$TODO_ID did not return 405"
    exit 1
fi
echo "PASS: GET on /todos/{id} returns 405"

# F4-05: Verify PUT /todos/{id} returns 405
PUT_ON_ID=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X PUT -H "Content-Type: application/json" -d '{"title":"Updated"}' http://localhost:$PORT/todos/$TODO_ID)
PUT_ON_ID_CODE=$(echo "$PUT_ON_ID" | grep "HTTP_CODE:" | cut -d: -f2)
echo "PUT /todos/$TODO_ID: HTTP $PUT_ON_ID_CODE"

if [ "$PUT_ON_ID_CODE" != "405" ]; then
    echo "FAIL: PUT /todos/$TODO_ID did not return 405"
    exit 1
fi
echo "PASS: PUT on /todos/{id} returns 405"

# F4-06: Verify create -> delete -> list absence workflow end-to-end
END_TO_END_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST -H "Content-Type: application/json" -d '{"title":"E2E Delete Test"}' http://localhost:$PORT/todos)
END_TO_END_CODE=$(echo "$END_TO_END_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
END_TO_END_BODY=$(echo "$END_TO_END_RESPONSE" | grep -v "HTTP_CODE:")
echo "POST E2E test: $END_TO_END_BODY (HTTP $END_TO_END_CODE)"

if [ "$END_TO_END_CODE" != "201" ]; then
    echo "FAIL: POST for E2E test did not return 201"
    exit 1
fi

E2E_TODO_ID=$(echo "$END_TO_END_BODY" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "E2E todo ID: $E2E_TODO_ID"

DELETE_E2E=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X DELETE http://localhost:$PORT/todos/$E2E_TODO_ID)
DELETE_E2E_CODE=$(echo "$DELETE_E2E" | grep "HTTP_CODE:" | cut -d: -f2)
echo "DELETE E2E: HTTP $DELETE_E2E_CODE"

if [ "$DELETE_E2E_CODE" != "204" ]; then
    echo "FAIL: DELETE E2E did not return 204"
    exit 1
fi

GET_AFTER_E2E=$(curl -s http://localhost:$PORT/todos)
if echo "$GET_AFTER_E2E" | grep -q "\"title\":\"E2E Delete Test\""; then
    echo "FAIL: E2E deleted todo still appears"
    exit 1
fi
echo "PASS: Create -> Delete -> List workflow verified"

echo "ALL TESTS PASSED"
exit 0