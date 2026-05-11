# Change Evidence Packet v0

This packet is evidence organization, not merge, release, compliance, production trust, or quality approval.

## Executive Summary

- Source change: https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/19 PR-19.
- Packet state: draft.
- Selected evidence profile: change-host-rich-v0.
- Required rows preserve pass, partial, fail, cannot_verify, not_assessed, and not_in_scope states without a score.
- Next decision ownership is recorded separately from approval.

## Packet Metadata

| field | value |
| --- | --- |
| packet_id | github-pr-19-change-evidence-packet |
| schema | change-evidence-packet.v0 |
| generated_from | https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/19 |
| generated_at | 2026-05-11T21:43:36Z |
| authoring_method | tool_generated |
| selected_profile | change-host-rich-v0 |
| redaction_policy | not_assessed |
| bundle_ref | github-pr-19-change-evidence-packet-bundle |
| packet_state | draft |

## Required Rows

| row id | state | answer | evidence refs | gap / next evidence | owner |
| --- | --- | --- | --- | --- | --- |
| PC-CHANGE | pass | Change-host metadata and commit range are retained. | github:pr, git:commit-range | none | maintainer |
| PC-INITIATOR | partial | PR body task source is retained. | github:pr-body | PR body is weaker than a dedicated issue binding | maintainer |
| PC-AGENT-ROUTE | partial | Agent route refs are retained. | agent:route | route refs are input refs, not a complete observed delegation chain | maintainer |
| PC-MUTATION | pass | Commit range and changed files are retained. | git:commit-range | none | maintainer |
| PC-VERIFICATION | pass | GitHub check and retained artifact evidence are retained. | github:check, artifact:change-evidence-packets, artifact:evidence-bundles | none | maintainer |
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

Manifest: `github-pr-19-change-evidence-packet-bundle`

| ref | source class | retained form | redaction status | resolver |
| --- | --- | --- | --- | --- |
| github:pr | change_host | external_ref | not_needed | https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/pull/19 |
| git:commit-range | git | external_ref | not_needed | 14d39cac7bc1b1e7437a0b4f20949c21479ae6c5..a5d167c93ff8450eb27ceb66c2e10cc4fbe801fd |
| theater:builder | witness | raw | not_needed | sdp-trace packet build-github |
| decision:owners | manual | raw | not_needed | default generated decision owners |
| gap:generated | manual | raw | not_needed | generated residual gaps |
| github:pr-body | change_host | external_ref | not_needed | .evidence/feature-ping-opencode/prompt-metadata.json |
| agent:route | harness | external_ref | not_needed | .evidence/feature-ping-opencode/manifest.json, .evidence/feature-ping-opencode/prompt-metadata.json, .evidence/feature-ping-gsd-review-repair/run |
| github:check | ci | external_ref | not_needed | build-and-test=https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/actions/runs/25698160567/job/75451640614 |
| github:review | review | external_ref | not_needed | codex-subagent-feature-review=thread-notification:019e18c8-df18-7512-9316-1f6a587bb86d |
| artifact:change-evidence-packets | ci | external_ref | not_needed | https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/actions/runs/25698160567/artifacts/6930226068 |
| artifact:evidence-bundles | ci | external_ref | not_needed | https://github.com/fall-out-bug/sdp-trace-demo-jvm-gsd/actions/runs/25698160567/artifacts/6930226324 |

## Residual Gaps

| row id | state | reason | closure evidence |
| --- | --- | --- | --- |
| PC-INITIATOR | partial | PR body is weaker than a dedicated issue binding | provide retained evidence for PC-INITIATOR |
| PC-AGENT-ROUTE | partial | route refs are input refs, not a complete observed delegation chain | provide retained evidence for PC-AGENT-ROUTE |
| PC-AUTHORITY | not_assessed | authority profile was not provided | provide retained evidence for PC-AUTHORITY |
| PC-ATTESTATION | not_assessed | signed trust inputs were not provided | provide retained evidence for PC-ATTESTATION |
| PC-DECISION | not_assessed | decision owners are placeholders, not bound approval or ownership evidence | provide retained evidence for PC-DECISION |

## What This Packet Does Not Prove

This packet does not approve merge, release, compliance, production trust, semantic correctness, or signed external trust.

