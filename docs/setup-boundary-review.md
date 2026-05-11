# Setup Boundary Review

Date: 2026-05-11
Branch: `demo-v2-packetization`

## Verdict

Current dirty changes are setup/build-system repair only. They may be used as
setup evidence, but they must not close feature packet rows.

## Reviewed Scope

- `.github/workflows/ci.yaml`
- `.github/pull_request_template.md`
- `.gitignore`
- `.ignore`
- `.opencode/opencode.json`
- `.sdp-trace/demo-profile.json`
- `.sdp-trace/packets/.gitkeep`
- `.sdp-trace/bundles/.gitkeep`
- `.bazelversion`
- `.planning/phases/01-project-skeleton/*.md`
- `MODULE.bazel`
- `MODULE.bazel.lock`
- `WORKSPACE`
- `README.md`
- `app/BUILD.bazel`
- `.evidence/local-build-test/...`

## Findings

| severity | finding | disposition |
| --- | --- | --- |
| medium | `MODULE.bazel` and `app/BUILD.bazel` repair build/test wiring by adding `rules_shell` and loading `sh_test`; endpoint behavior is unchanged. | setup evidence only |
| medium | `.evidence/local-build-test/manifest.json` records `dirty_checkout: true`; CI witness and audit-grade gates remain `cannot_verify`. | local setup evidence only |
| medium | `.evidence/local-build-test/report/evidence-table.json` marks the run unmatched to contract evidence. | cannot close feature rows |
| low | README and `.planning` changes are documentation/setup-path changes. | setup evidence only |
| low | `.ignore`, `.gitignore`, and `.opencode/opencode.json` are tool isolation/config changes. | setup evidence only |

## Non-Claims

- No application feature behavior was implemented or repaired by Codex in this
  setup slice.
- No feature packet row is closed by this setup slice.
- No CI witness, audit-grade witness, source-bound release proof, merge
  approval, release approval, production trust, or semantic quality approval is
  claimed.
