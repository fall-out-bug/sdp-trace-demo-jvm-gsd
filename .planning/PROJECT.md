# Project: Feature Flag & Entitlement Service

**Type:** Greenfield Kotlin/JVM Bazel application

**Core Value:** A centralized service enabling teams to control feature releases and user entitlements through flag management APIs, entitlement evaluation, and audit logging.

---

## Domain Context

Feature flags are a standard mechanism for controlling feature availability in software systems. This service manages flag definitions, evaluates user entitlements against targeting rules, and provides audit trails for compliance and debugging.

**Key concepts:**
- **Flag**: A named feature toggle with a boolean or variant value
- **Targeting rule**: A condition that determines flag value based on user attributes
- **Entitlement**: The resolved flag value for a specific user context
- **Audit log**: Immutable record of evaluations and flag changes

---

## Technical Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin |
| Build | Bazel |
| HTTP Server | Ktor (embedded) |
| Database | SQLite (embedded, file-based) |
| Serialization | Kotlinx Serialization |
| Testing | JUnit 5, Kotest |

---

## Architecture

```
┌─────────────────┐
│  Admin CLI       │  ← Phase 5
├─────────────────┤
│  Audit Layer    │  ← Phase 4
├─────────────────┤
│  Evaluation     │  ← Phase 3
│  Service        │
├─────────────────┤
│  Flag API       │  ← Phase 2
│  Service       │
├─────────────────┤
│  Persistence    │
│  Layer          │
└─────────────────┘
```

---

## Non-Functional Requirements

| Requirement | Target |
|--------------|--------|
| Evaluation latency | <100ms p99 |
| Availability | 99.9% |
| Flag count support | 10,000+ |
| Evaluations/sec | 1,000+ |

---

## Key Decisions

| Id | Decision | Rationale |
|----|----------|-----------|
| D-01 | Use embedded SQLite | Simplifies deployment, no external DB for v1 |
| D-02 | Use Ktor for HTTP | Lightweight, idiomatic Kotlin |
| D-03 | API key authentication | Simpler than OAuth for service-to-service |
| D-04 | Soft delete for flags | Preserves audit trail integrity |

---

## Out of Scope

| Feature | Deferred To |
|---------|------------|
| OAuth/SSO | v2 |
| GraphQL API | v2 |
| External SQL DB | v2 |
| A/B testing | v2 |
| Real-time push | v2 |

---

*Project created: 2025-01-14*