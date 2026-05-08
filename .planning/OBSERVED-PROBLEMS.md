# Observed Problems

This file records real setup and planning issues observed during dogfood. Do not add synthetic negative cases.

## 2026-05-08

### GSD OpenCode install command drift

Initial setup used the wrong OpenCode plugin path. The corrected local install command is:

```bash
npx get-shit-done-cc@latest --opencode --local --yes
```

Current state: corrected and committed in `eefcac8`.

### GSD planning defaulted to auto-advance behavior

`/gsd-new-project` generated `.planning/config.json` with `mode: yolo`, `workflow.auto_advance: true`, and `commit_docs: true`, then committed planning artifacts without waiting for user review.

Current state: corrected in planning artifacts. The repository now uses `mode: interactive`, `workflow.auto_advance: false`, `commit_docs: false`, and an explicit user approval boundary before product implementation.

### Generated plan overclaimed build setup

The generated project plan stated that the Bazel workspace was configured even though no Bazel/Kotlin files exist yet.

Current state: corrected to `planned, not implemented yet`.

### OpenCode command invocation mismatch

Passing `/gsd-discuss-phase` and `/gsd-plan-phase` as plain prompt text did not reliably invoke the GSD commands. The working invocation used OpenCode command mode:

```bash
opencode run --command gsd-discuss-phase ...
opencode run --command gsd-plan-phase ...
```

Current state: corrected during Phase 1 planning and execution.

### Generated implementation summary overclaimed verification

GSD marked Phase 1 complete while the local Bazel test target was still failing. The failure was real, not synthetic: the generated `kt_jvm_test` setup attempted to use a runner configuration that did not execute the Kotlin tests correctly in this repository.

Current state: repaired by using an explicit Kotlin test main class and rerunning Bazel build/test successfully.

### Vendored GSD runtime has upstream whitespace noise

The installed `.opencode/get-shit-done` runtime contains upstream Markdown trailing whitespace. Treating that vendored runtime as demo product code makes `git diff --check` noisy and obscures actionable demo checks.

Current state: CI structural diff checks exclude `.opencode/**` and record the check result for the actual demo/repo artifacts.
