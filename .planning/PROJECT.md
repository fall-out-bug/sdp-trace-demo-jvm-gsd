# Entitlement Validation Library

## What This Is

A Kotlin/JVM + Bazel demo for an in-memory entitlement validation library. The planned library validates user entitlements against configurable rules and returns decision records that can be audited.

## Core Value

Accurate in-memory entitlement validation with traceable decision audit logs.

## Requirements

### Validated

None yet.

### Active

- [ ] ENT-01: User can validate single entitlement against rules
- [ ] ENT-02: User can configure entitlement rules programmatically
- [ ] ENT-03: User receives decision audit log with validation result
- [ ] ENT-04: User can override entitlement features (future phase)
- [ ] ENT-05: User can query decision audit trail (future phase)
- [ ] ENT-06: Expired entitlements are denied (future phase)
- [ ] ENT-07: Manual risk override capability (future phase)

### Out of Scope

- Persistent storage - in-memory only for v1
- Network/distributed entitlement lookup - local rules only
- OAuth/SSO integration - separate future phase
- Database-backed audit logs - in-memory log only

## Context

**Technical Environment:**
- Kotlin/JVM with Bazel build system
- Target: kt_jvm_library and kt_jvm_test rules
- No Java source — Kotlin only

**Build System:**
- Bazel workspace is implemented for Phase 1.
- Expected target shape: `kt_jvm_library` and `kt_jvm_test`.
- Kotlin rule choice is verified by local and GitHub Actions Bazel build/test.

**Verification Approach:**
- Repository observer state is recorded by GitHub Actions artifact `sdp-trace-observer`.
- This planning document is not proof. CI artifact metadata, Bazel logs, and exit-code files are the external evidence for Phase 1.

## Constraints

- **Tech Stack**: Kotlin/JVM + Bazel only — no Java source, no Gradle
- **Storage**: In-memory - no persistence
- **Planning Scope**: New phase planning is artifact-only until that phase is approved
- **Approval Gate**: Do not run `/gsd-plan-phase`, `/gsd-execute-phase`, or product-code edits until the user approves the reviewed GSD plan.
- **Runtime**: OpenCode model minimax-coding-plan/MiniMax-M2.5

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Bazel over Gradle | User specified Bazel | Pending implementation |
| Kotlin-only (no Java) | User specified | Pending implementation |
| In-memory validation | User specified | Pending implementation |
| Separate phases per feature | Each feature planned as branch/PR/CI work | Pending implementation |

---

## Feature Sequence (Planned)

| Feature | Phase | Description |
|---------|-------|-------------|
| plan-entitlement | Phase 1 | Core in-memory entitlement validation |
| feature-override | Phase 2 | Override entitlement features |
| decision-audit | Phase 3 | Query decision audit trail |
| expired-entitlement-denial | Phase 4 | Deny expired entitlements |
| manual-risk-override | Phase 5 | Manual risk override capability |

---

*Last updated: 2026-05-08 after project initialization cleanup*
