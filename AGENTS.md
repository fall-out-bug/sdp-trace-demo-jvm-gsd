# AGENTS.md - Entitlement Validation Library Demo

## Scope

This repository is a Kotlin/JVM + Bazel demo for an in-memory entitlement validation library.

Use GSD through OpenCode with model `minimax-coding-plan/MiniMax-M2.5`.

## Workflow

Current status: Phase 1 implementation is in progress/review.

Do not write product code for a new phase until its GSD-generated plan is reviewed and approved.

Phase 1 approval was granted after GSD discussion, GSD planning, and corrective review. Later phases must repeat the same approval boundary before implementation.

## Key Constraints

1. **Approval gate** - planning review before implementation.
2. **Bazel build** — kt_jvm_library and kt_jvm_test only
3. **Kotlin-only** — No Java source files
4. **In-memory** — No persistent storage
5. **No fabricated failures** — record only observed failures or gaps.

## Phase Sequence

| Phase | Feature | Notes |
|-------|---------|-------|
| 1 | Core In-Memory Entitlement Validation | Base validation and audit, under review |
| 2 | Feature Override Capability | Override rules per feature |
| 3 | Decision Audit Trail | Query historical decisions |
| 4 | Expired Entitlement Denial | Time-window rules |
| 5 | Manual Risk Override | Privileged user override |
