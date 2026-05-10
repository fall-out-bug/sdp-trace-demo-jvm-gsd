# Project: Feature Flag & Entitlement Service

**Core Value:** A Kotlin/JVM service that enables teams to control feature releases and user entitlements through a centralized flag management system with evaluation APIs and audit capabilities.

## Overview

This is a greenfield Bazel-based Kotlin/JVM application providing:
- **Flag Definition API** — Create, update, and manage feature flags with metadata and targeting rules
- **Entitlement Evaluation API** — Evaluate whether users are entitled to features based on flag rules
- **Audit Logging** — Persist and query audit trails of all evaluations and flag changes
- **Admin CLI** — Command-line interface for administrative operations and smoke testing

The service is built with Bazel for build management and uses a traditional layered architecture (HTTP layer → service layer → persistence layer).

---

## Phases

- [ ] **Phase 1: Project Skeleton** - Bazel build, CI, and basic project structure
- [ ] **Phase 2: Flag Definition API** - CRUD operations for feature flags
- [ ] **Phase 3: Entitlement Evaluation API** - Evaluate user entitlements against flags
- [ ] **Phase 4: Audit Log Persistence** - Persist evaluation and change logs
- [ ] **Phase 5: Admin CLI Smoke Workflow** - CLI tools and end-to-end verification

---

## Phase 1: Project Skeleton

**Goal**: Establish a working Bazel build, CI pipeline, and runnable skeleton application

**Depends on**: Nothing (first phase)

**Requirements**: SETUP-01, SETUP-02, SETUP-03, SETUP-04, SETUP-05

**Success Criteria** (what must be TRUE):
  1. `bazel build //...` compiles without errors
  2. `bazel test //...` runs and passes
  3. CI pipeline triggers on pull request and runs tests
  4. Application starts and exposes health endpoint
  5. Code is lint-free and formatted

**Plans**: 5 plans

Plans:
- [x] 01-01-PLAN.md — Bazel workspace and build configuration
- [x] 01-02-PLAN.md — Ktor application with health endpoint
- [⚠] 01-03-PLAN.md — Test infrastructure with JUnit5/Kotest (BLOCKED: Bazel 9)
- [x] 01-04-PLAN.md — GitHub Actions CI pipeline
- [x] 01-05-PLAN.md — Code formatting with ktlint

---

## Phase 2: Flag Definition API

**Goal**: Users can create, read, update, delete, and list feature flags via HTTP API

**Depends on**: Phase 1

**Requirements**: FLAG-01, FLAG-02, FLAG-03, FLAG-04, FLAG-05

**Success Criteria** (what must be TRUE):
  1. User can create a flag with name, description, and default value via POST
  2. User can retrieve a flag by ID via GET
  3. User can update flag metadata and rules via PUT/PATCH
  4. User can delete a flag (soft delete) via DELETE
  5. User can list all flags with pagination via GET

**Plans**: TBD

---

## Phase 3: Entitlement Evaluation API

**Goal**: Systems can query user entitlements with attribute-based targeting

**Depends on**: Phase 2

**Requirements**: EVAL-01, EVAL-02, EVAL-03, EVAL-04, EVAL-05

**Success Criteria** (what must be TRUE):
  1. System can evaluate a single flag for a user context via POST
  2. System can batch evaluate multiple flags in one request
  3. Targeting rules support attribute matching (equals, contains, oneOf)
  4. Evaluation responds within latency SLA
  5. Invalid flag or user context returns clear error

**Plans**: TBD

---

## Phase 4: Audit Log Persistence

**Goal**: All flag evaluations and changes are persisted to database and queryable

**Depends on**: Phase 3

**Requirements**: AUDIT-01, AUDIT-02, AUDIT-03, AUDIT-04

**Success Criteria** (what must be TRUE):
  1. Every evaluation is logged with timestamp, user context, and result
  2. Every flag change is logged with actor and before/after state
  3. Admin can query audit logs with filters (date range, flag, user)
  4. Audit logs are retained per retention policy

**Plans**: TBD

---

## Phase 5: Admin CLI Smoke Workflow

**Goal**: Operators can manage flags and verify system health via CLI

**Depends on**: Phase 4

**Requirements**: CLI-01, CLI-02, CLI-03, CLI-04

**Success Criteria** (what must be TRUE):
  1. Operator can create/update/delete flags via CLI command
  2. Operator can check entitlement for a user via CLI
  3. Operator can run end-to-end smoke test that validates all operations
  4. CLI provides helpful error messages and usage documentation

**Plans**: TBD

---

## Phase Dependencies

```
Phase 1: Project Skeleton
           │
           ▼
Phase 2: Flag Definition API
           │
           ▼
Phase 3: Entitlement Evaluation API
           │
           ▼
Phase 4: Audit Log Persistence
           │
           ▼
Phase 5: Admin CLI Smoke Workflow
```

**Dependency chain notes:**
- Phase 2 depends on Phase 1 (needs skeleton to build API)
- Phase 3 depends on Phase 2 (needs flag storage to evaluate)
- Phase 4 depends on Phase 3 (needs evaluations to audit)
- Phase 5 depends on Phase 4 (needs full system for smoke test)

---

## Progress

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Project Skeleton | 2/5 | In progress | 2026-05-10 |
| 2. Flag Definition API | 0/TBD | Not started | - |
| 3. Entitlement Evaluation API | 0/TBD | Not started | - |
| 4. Audit Log Persistence | 0/TBD | Not started | - |
| 5. Admin CLI Smoke Workflow | 0/TBD | Not started | - |

---

*Roadmap created: 2025-01-14*
*Last updated: 2025-01-14*