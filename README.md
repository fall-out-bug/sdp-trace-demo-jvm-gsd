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

## Demo PR And Evidence Ledger

This repository demonstrates feature-by-feature delivery with
[`sdp-trace`](https://github.com/fall-out-bug/sdp-trace) as the evidence and
flight-recorder layer. Application features are delivered as normal demo PRs;
`sdp-trace` records route evidence, packet rows, retained artifacts, and
explicit non-claims.

| PR | State | Demo slice | Primary evidence |
|----|-------|------------|------------------|
| [#16](https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/16) | merged | Packetized readiness slice | `.sdp-trace/packets/feature-1.md`, `.sdp-trace/bundles/feature-1/` |
| [#17](https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/17) | merged | Liveness endpoint slice | `.sdp-trace/packets/feature-2.md`, `.sdp-trace/bundles/feature-2/` |
| [#18](https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/18) | merged | Version endpoint slice | `.sdp-trace/packets/feature-3.md`, `.sdp-trace/bundles/feature-3/` |
| [#19](https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/19) | merged | Ping endpoint slice | `.sdp-trace/packets/feature-4.md`, `.sdp-trace/bundles/feature-4/` |
| [#20](https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/20) | merged | Info endpoint slice | `.sdp-trace/packets/feature-5.md`, `.sdp-trace/bundles/feature-5/` |
| [#22](https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/22) | merged | Flags list endpoint slice | `.sdp-trace/packets/feature-6/`, `.sdp-trace/runs/feature-6-*/` |
| [#21](https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/21) | open draft | Negative theater demo | `.sdp-trace/packets/negative.md`, `.sdp-trace/bundles/negative/`, `.evidence/negative-*` |

The negative draft PR is intentionally left open. It demonstrates an
agent-claimed verification case where independent artifact evidence is absent,
so the expected packet state is `PC-VERIFICATION: cannot_verify` and
`PC-THEATER: partial`.

GitHub Actions uploads retained packet artifacts for PR and main runs:

- `change-evidence-packets`
- `evidence-bundles`

Feature 6 uses the newer live PR packet path from `sdp-trace`: `packet
build-pr` binds packet verification to retained GitHub Actions run and artifact
evidence. The packet remains evidence organization only; it is not merge,
release, production-trust, compliance, or semantic-quality approval.

## Review Artifacts

For semantic quality assessment, refer to review files in this repository, test
output for functional verification, and manual code review for design quality.
