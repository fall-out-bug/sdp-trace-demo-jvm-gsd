# Roadmap: Entitlement Validation Library

**Created:** 2026-05-08
**Project:** Entitlement Validation Library
**Mode:** coarse

## Summary

| Phase | Goal | Requirements | Status |
|-------|------|---------------|--------|
| 1 | Core In-Memory Entitlement Validation | ENT-01, ENT-02, ENT-03, ENT-04, ENT-05, ENT-06 | 1 plan created |
| 2 | Feature Override Capability | ENT-07, ENT-08 | Pending |
| 3 | Decision Audit Trail | ENT-09, ENT-10, ENT-11 | Pending |
| 4 | Expired Entitlement Denial | ENT-12, ENT-13 | Pending |
| 5 | Manual Risk Override | ENT-14, ENT-15 | Pending |

---

## Phase 1: Core In-Memory Entitlement Validation

**Goal:** Implement core in-memory entitlement validation with configurable rules and audit logging.

**Requirements:**
- ENT-01: System can validate single entitlement against configured rules
- ENT-02: User can configure entitlement rules programmatically via API
- ENT-03: Validation result includes decision audit log with context
- ENT-04: Validation is purely in-memory (no external storage calls)
- ENT-05: Rules can be added/removed at runtime
- ENT-06: Rules support common attribute matching (user role, group, custom attributes)

**Success Criteria:**

1. Entitlement validation returns correct allow/deny decision
2. Rules can be configured programmatically before validation
3. Decision audit log captures all validation context
4. All validation happens in-memory with no external calls
5. Unit tests validate core validation logic

**Implementation Notes:**
- Kotlin-only, no Java source
- Bazel build system (kt_jvm_library, kt_jvm_test)
- Each future feature is separate branch/PR/CI work

---

## Phase 2: Feature Override Capability

**Goal:** Allow users to override entitlement for specific features.

**Requirements:**
- ENT-07: User can override entitlement for specific features
- ENT-08: Override can be feature-scoped or globally scoped

**Success Criteria:**

1. Feature-level override takes precedence over base rules
2. Global override applies across all features
3. Override configuration is programmatically accessible

---

## Phase 3: Decision Audit Trail

**Goal:** Enable querying of decision audit trail.

**Requirements:**
- ENT-09: User can query decision audit trail
- ENT-10: Audit logs support filtering by user, timestamp, decision type
- ENT-11: Audit logs support pagination

**Success Criteria:**

1. Historical validation decisions are queryable
2. Filter by user returns correct subset
3. Filter by timestamp range works correctly
4. Pagination returns expected page sizes

---

## Phase 4: Expired Entitlement Denial

**Goal:** Automatically deny expired entitlements.

**Requirements:**
- ENT-12: Expired entitlements are denied
- ENT-13: Time window rules for entitlements (valid-from, valid-until)

**Success Criteria:**

1. Entitlements with past expiration are denied
2. Entitlements with future valid-from are denied before start
3. Entitlements within valid window are allowed

---

## Phase 5: Manual Risk Override

**Goal:** Provide manual risk override capability for privileged users.

**Requirements:**
- ENT-14: Manual risk override capability for privileged users
- ENT-15: Override requires justification and approval chain

**Success Criteria:**

1. Privileged users can override any decision
2. Override requires non-empty justification
3. Override decisions are logged with justification

---

## Phase Dependencies

```
Phase 1 (Core Validation)
    │
    ├──► Phase 2 (Feature Override) - requires Phase 1
    │
    ├──► Phase 3 (Decision Audit) - requires Phase 1
    │
    ├──► Phase 4 (Expiration) - requires Phase 1
    │
    └──► Phase 5 (Manual Override) - requires Phase 3 (audit trail)
```

---

## Execution Boundary

This roadmap is planning-only until user approval. The next allowed GSD action is review/discussion of Phase 1 scope, not implementation.

Each implementation phase must run as separate branch/PR/CI/evidence/trace work. If CI or artifact proof is absent, record it as `not_assessed`, not green.

---

*Roadmap created: 2026-05-08*
*Last updated: 2026-05-08 after project initialization cleanup*
