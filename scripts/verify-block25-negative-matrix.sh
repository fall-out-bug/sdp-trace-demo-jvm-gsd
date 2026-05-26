#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -ne 1 ]; then
  echo "usage: verify-block25-negative-matrix.sh <matrix-json>" >&2
  exit 2
fi

matrix=$1

jq -e '.schema_version == "sdp-trace-demo-block25-negative-matrix-v1"' "$matrix" >/dev/null

require_case() {
  local id=$1
  local state=$2
  local reason=$3
  jq -e --arg id "$id" --arg state "$state" --arg reason "$reason" '
    [.cases[]
      | select(
          .id == $id
          and .expected_state == $state
          and .reason_code == $reason
          and .independent_of_clean_artifact_index == true
        )] | length == 1
  ' "$matrix" >/dev/null
}

require_case missing-ci-oidc cannot_verify missing_ci_oidc
require_case stale-artifact-digest fail artifact_digest_mismatch
require_case source-run-mismatch fail source_run_mismatch

case_count=$(jq '.cases | length' "$matrix")
if [ "$case_count" -ne 3 ]; then
  echo "expected exactly 3 Block 25 negative cases, got $case_count" >&2
  exit 1
fi
