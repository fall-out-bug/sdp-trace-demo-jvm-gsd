# Todo Service JVM/GSD Demo

This is a minimal todo REST API demo application built with Kotlin, Bazel, and
JDK HttpServer. It is intentionally small so a developer can work through normal
GSD planning, implementation, review, and verification without learning a custom
demo harness first.

## Demo Purpose

This demo provides a clear implementation baseline for an AI-assisted delivery
run. The application is the thing the developer sees: a lightweight REST API
with five features and ordinary build/test commands.

## Features

| Feature | Endpoint | Description |
|---------|----------|-------------|
| 1 | GET /health | Health check returning status and timestamp |
| 2 | GET/POST /todos | List all todos, create new todo with title |
| 3 | POST /todos/{id}/complete | Mark a todo as completed |
| 4 | DELETE /todos/{id} | Delete a todo by ID |
| 5 | GET /stats | Return todo statistics (total, completed, active counts) |
| 6 | GET /flags | List all feature flags (read-only, returns empty array) |

## Running Tests

```bash
# Build all targets
bazel build //...

# Run smoke tests (includes stats coverage)
bazel test //app:smoke_test --test_output=all

# Run locally (for manual testing)
bazel run //app:app
# Then test with curl:
# curl http://localhost:8080/health
# curl http://localhost:8080/todos
# curl -X POST -H "Content-Type: application/json" -d '{"title":"My task"}' http://localhost:8080/todos
# curl -X POST http://localhost:8080/todos/todo-1/complete
# curl -X DELETE http://localhost:8080/todos/todo-1
# curl http://localhost:8080/stats
# curl http://localhost:8080/flags
```

## Review Artifacts

For semantic quality assessment, refer to review files in this repository, test
output for functional verification, and manual code review for design quality.
