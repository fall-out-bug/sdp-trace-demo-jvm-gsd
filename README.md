# SDP Trace Demo JVM/GSD

This is a minimal todo REST API demo application that demonstrates GSD (Guaranteed Shipping with Evidence) principles. It uses JDK HttpServer and Kotlin without frameworks, serving as abaseline for evidence collection using sdp-trace.

## Demo Purpose

This demo showcases a lightweight REST API with five features, providing a clear implementation baseline for evidence collection. The purpose is to demonstrate how [sdp-trace](https://github.com/fall-out-bug/sdp-trace) records harness/process evidence from build and test execution without inferring semantic quality.

**Important**: sdp-trace records harness/process evidence (build success, test pass/fail, execution traces) and does NOT infer semantic quality. Quality assessment requires review/test evidence from human evaluators or separate evaluation frameworks.

## Features

| Feature | Endpoint | Description |
|---------|----------|-------------|
| 1 | GET /health | Health check returning status and timestamp |
| 2 | GET/POST /todos | List all todos, create new todo with title |
| 3 | POST /todos/{id}/complete | Mark a todo as completed |
| 4 | DELETE /todos/{id} | Delete a todo by ID |
| 5 | GET /stats | Return todo statistics (total, completed, active counts) |

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
```

## sdp-trace Evidence Conventions

This demo is linked to [sdp-trace](https://github.com/fall-out-bug/sdp-trace) which provides:

- **Harness evidence**: Build artifacts, test execution results, process traces
- **Process evidence**: Execution flow, timing, resource usage
- **NOT semantic quality**: The trace records WHAT executed and HOW, not whether the behavior is CORRECT

Evidence collection follows sdp-trace conventions:
- Build outcomes are captured from Bazel execution
- Test results include pass/fail status and output
- Process traces record execution flow without inferring correctness

For semantic quality assessment, refer to:
- REVIEW.md files in this repository for human review artifacts
- Test output for functional verification
- Manual code review for design quality

## Repository

- **sdp-trace**: https://github.com/fall-out-bug/sdp-trace
- **Demo repo**: https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd