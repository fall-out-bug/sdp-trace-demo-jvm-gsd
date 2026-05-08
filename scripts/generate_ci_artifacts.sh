#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CI_ARTIFACTS_DIR="$REPO_ROOT/ci-artifacts"

mkdir -p "$CI_ARTIFACTS_DIR/provenance"
mkdir -p "$CI_ARTIFACTS_DIR/evidence"
mkdir -p "$CI_ARTIFACTS_DIR/trace"

GITHUB_REPOSITORY="${GITHUB_REPOSITORY:-local}"
GITHUB_RUN_ID="${GITHUB_RUN_ID:-local}"
GITHUB_RUN_ATTEMPT="${GITHUB_RUN_ATTEMPT:-local}"
GITHUB_SHA="${GITHUB_SHA:-local}"
GITHUB_REF_NAME="${GITHUB_REF_NAME:-local}"
GITHUB_EVENT_NAME="${GITHUB_EVENT_NAME:-local}"

cat > "$CI_ARTIFACTS_DIR/provenance/ci-provenance.json" << EOF
{
  "artifact_kind": "provenance",
  "generated_by_ci": true,
  "repo": "$GITHUB_REPOSITORY",
  "run_id": "$GITHUB_RUN_ID",
  "run_attempt": "$GITHUB_RUN_ATTEMPT",
  "commit_sha": "$GITHUB_SHA",
  "ref_name": "$GITHUB_REF_NAME",
  "event_name": "$GITHUB_EVENT_NAME",
  "verification_commands": [
    "jq validate gsd/features.json",
    "bazel test //...",
    "bash scripts/bootstrap_check.sh"
  ],
  "scope": "run"
}
EOF

cat > "$CI_ARTIFACTS_DIR/evidence/ci-evidence.json" << EOF
{
  "artifact_kind": "evidence",
  "generated_by_ci": true,
  "repo": "$GITHUB_REPOSITORY",
  "run_id": "$GITHUB_RUN_ID",
  "run_attempt": "$GITHUB_RUN_ATTEMPT",
  "commit_sha": "$GITHUB_SHA",
  "ref_name": "$GITHUB_REF_NAME",
  "event_name": "$GITHUB_EVENT_NAME",
  "verification_commands": [
    "jq validate gsd/features.json",
    "bazel test //...",
    "bash scripts/bootstrap_check.sh"
  ],
  "scope": "run"
}
EOF

cat > "$CI_ARTIFACTS_DIR/trace/ci-trace.json" << EOF
{
  "artifact_kind": "trace",
  "generated_by_ci": true,
  "repo": "$GITHUB_REPOSITORY",
  "run_id": "$GITHUB_RUN_ID",
  "run_attempt": "$GITHUB_RUN_ATTEMPT",
  "commit_sha": "$GITHUB_SHA",
  "ref_name": "$GITHUB_REF_NAME",
  "event_name": "$GITHUB_EVENT_NAME",
  "verification_commands": [
    "jq validate gsd/features.json",
    "bazel test //...",
    "bash scripts/bootstrap_check.sh"
  ],
  "scope": "run"
}
EOF

echo "CI artifacts generated:"
echo "  - $CI_ARTIFACTS_DIR/provenance/ci-provenance.json"
echo "  - $CI_ARTIFACTS_DIR/evidence/ci-evidence.json"
echo "  - $CI_ARTIFACTS_DIR/trace/ci-trace.json"