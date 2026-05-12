# Change Evidence Packet v0

This packet is evidence organization, not merge, release, compliance, production trust, or quality approval.

## Executive Summary

- Source change: https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/22 PR-22.
- Packet state: draft.
- Selected evidence profile: change-host-rich-v0.
- Required rows preserve pass, partial, fail, cannot_verify, not_assessed, and not_in_scope states without a score.
- Next decision ownership is recorded separately from approval.

## Packet Metadata

| field | value |
| --- | --- |
| packet_id | github-pr-22-change-evidence-packet |
| schema | change-evidence-packet.v0 |
| generated_from | https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/22 |
| generated_at | 2026-05-12T07:03:02Z |
| authoring_method | tool_generated |
| selected_profile | change-host-rich-v0 |
| redaction_policy | not_assessed |
| bundle_ref | github-pr-22-change-evidence-packet-bundle |
| packet_state | draft |

## Required Rows

| row id | state | answer | evidence refs | gap / next evidence | owner |
| --- | --- | --- | --- | --- | --- |
| PC-CHANGE | pass | Change-host metadata and commit range are retained. | github:pr, git:commit-range | none | maintainer |
| PC-INITIATOR | partial | PR body task source is retained. | github:pr-body | PR body is weaker than a dedicated issue binding | maintainer |
| PC-AGENT-ROUTE | partial | Agent route refs and digest-only prompt boundary are retained. | agent:route, prompt:boundary | prompt text is unavailable; digest-only boundary supports partial route proof | maintainer |
| PC-MUTATION | pass | Commit range and changed files are retained. | git:commit-range | none | maintainer |
| PC-VERIFICATION | pass | GitHub check and retained artifact evidence are retained for workflow run 25717933440. | github:check, artifact:evidence-bundles, artifact:change-evidence-packets | none | maintainer |
| PC-REVIEW | pass | Review evidence is retained. | github:review | none | maintainer |
| PC-AUTHORITY | not_assessed | Authority was not assessed for this generated GitHub input. | none | authority profile was not provided | maintainer |
| PC-THEATER | pass | No P0 theater finding triggered by the minimal GitHub input builder. | theater:builder | none | maintainer |
| PC-ATTESTATION | not_assessed | Signed or external attestation was not assessed. | none | signed trust inputs were not provided | maintainer |
| PC-DECISION | not_assessed | Default decision owner placeholders are recorded. | decision:owners | decision owners are placeholders, not bound approval or ownership evidence | maintainer |
| PC-RESIDUAL-GAPS | partial | Non-pass rows remain explicit in residual gaps. | gap:generated | generated packet contains explicit non-pass rows | maintainer |

## Theater Findings

| reason code | state | severity | finding | trigger evidence | required closure evidence |
| --- | --- | --- | --- | --- | --- |
| none | pass | none | No P0 theater finding triggered by the minimal GitHub input builder. | PC-THEATER row | none |

## Decision Ownership

| decision | owner | state | reason |
| --- | --- | --- | --- |
| merge | maintainer | not_assessed | packet is not approval |
| release | release owner | not_assessed | packet is not release approval |
| risk_acceptance | risk owner | not_assessed | packet is not risk acceptance |
| security_review | security owner | not_assessed | packet is not security review |

## Evidence Bundle

Manifest: `github-pr-22-change-evidence-packet-bundle`

| ref | source class | retained form | redaction status | resolver |
| --- | --- | --- | --- | --- |
| github:pr | change_host | external_ref | not_needed | https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/22 |
| git:commit-range | git | external_ref | not_needed | 1241956..8cf49640f57d7e9ea2f4cf124394f21b171ee9ea |
| theater:builder | witness | raw | not_needed | sdp-trace packet build-pr |
| decision:owners | manual | raw | not_needed | default generated decision owners |
| gap:generated | manual | raw | not_needed | generated residual gaps |
| prompt:boundary | harness | digest_only | not_needed | prompt:digest:sha256:75d32eff8ce3f4d7a08601c97606020a4c9a6713fd46d0047597ab0b10b43091 |
| github:pr-body | change_host | external_ref | not_needed | https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/22 |
| agent:route | harness | external_ref | not_needed | .sdp-trace/runs/feature-6-opencode-minimax-free/run.json, .sdp-trace/runs/feature-6-opencode-repair-minimax-free/run.json, .sdp-trace/runs/feature-6-gsd-review-minimax/run.json |
| github:check | ci | external_ref | not_needed | build-and-test=https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/actions/runs/25717933440 |
| github:review | review | external_ref | not_needed | pi-deepseek-feature-review=pr-comment:pending |
| artifact:evidence-bundles | ci | external_ref | not_needed | https://api.github.com/repos/fall-out-bug/sdp-trace-demo-jvm-gsd/actions/artifacts/6937335231/zip |
| artifact:change-evidence-packets | ci | external_ref | not_needed | https://api.github.com/repos/fall-out-bug/sdp-trace-demo-jvm-gsd/actions/artifacts/6937335089/zip |

## Residual Gaps

| row id | state | reason | closure evidence |
| --- | --- | --- | --- |
| PC-INITIATOR | partial | PR body is weaker than a dedicated issue binding | provide retained evidence for PC-INITIATOR |
| PC-AGENT-ROUTE | partial | prompt text is unavailable; digest-only boundary supports partial route proof | provide retained evidence for PC-AGENT-ROUTE |
| PC-AUTHORITY | not_assessed | authority profile was not provided | provide retained evidence for PC-AUTHORITY |
| PC-ATTESTATION | not_assessed | signed trust inputs were not provided | provide retained evidence for PC-ATTESTATION |
| PC-DECISION | not_assessed | decision owners are placeholders, not bound approval or ownership evidence | provide retained evidence for PC-DECISION |

## What This Packet Does Not Prove

This packet does not approve merge, release, compliance, production trust, semantic correctness, or signed external trust.

