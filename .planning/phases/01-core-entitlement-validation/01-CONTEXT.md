# Phase 1 Context: Core In-Memory Entitlement Validation

**Phase:** 1
**Name:** Core In-Memory Entitlement Validation
**Created:** 2026-05-09

---

## Domain

Core in-memory entitlement validation with configurable rules and audit logging.

## Requirements (Locked by Roadmap)

- ENT-01: System can validate single entitlement against configured rules
- ENT-02: User can configure entitlement rules programmatically via API
- ENT-03: Validation result includes decision audit log with context
- ENT-04: Validation is purely in-memory (no external storage calls)
- ENT-05: Rules can be added/removed at runtime
- ENT-06: Rules support common attribute matching (user role, group, custom attributes)

## Decisions

### 1. Rule Data Structure

Use a small sealed rule hierarchy for type safety and readable exhaustive evaluation.

- Minimal Phase 1 set: allow/deny decisions from role, group, and custom attribute rules
- Avoid map-only untyped rules as the primary API

### 2. Attribute Matching Logic

Deterministic exact matching in Phase 1.

- Combine attributes within a rule with AND semantics
- Multiple rules evaluated in explicit list order
- No regex, wildcard, or expression language in Phase 1

### 3. Audit Record Format

Structured audit record with:

- Request ID
- User ID
- Entitlement/feature ID
- Decision (allow/deny)
- Matched rule IDs
- Evaluated rule IDs
- Deny/allow reason
- Timestamp

Kept in memory and queryable by future phases.

### 4. API Design

Small Kotlin API centered on `EntitlementValidator` plus plain Kotlin data classes.

- Direct construction and simple immutable config
- Avoid complex builders for Phase 1

### 5. Entitlement Input Model

Single rich request object.

- Request includes: user ID, entitlement/feature ID, roles, groups, custom attributes
- Batch validation is out of scope (future phase)

---

## Canonical Refs

- `.planning/ROADMAP.md` — Phase goal and requirements
- `.planning/PROJECT.md` — Technical constraints (Kotlin-only, Bazel, in-memory)
- `.planning/REQUIREMENTS.md` — Requirement definitions

---

## Out of Scope

- Persistent storage
- Batch validation
- Regex/wildcard matching in rules
- Complex expression languages
- Any product code (Kotlin source files)

---

*Context created: 2026-05-09*