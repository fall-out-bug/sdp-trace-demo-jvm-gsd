#!/usr/bin/env bash
set -euo pipefail

workspace=${BUILD_WORKSPACE_DIRECTORY:-$(pwd)}
matrix="$workspace/.sdp-trace/negative-matrix/block25-v2-negative-matrix.json"

"$workspace/scripts/verify-block25-negative-matrix.sh" "$matrix"

tmpdir=$(mktemp -d)
trap 'rm -rf "$tmpdir"' EXIT

artifact_root="$tmpdir/downloaded-artifacts"
mkdir -p "$artifact_root/change-evidence-packets"
cp "$workspace/.sdp-trace/packets/feature-1.md" "$artifact_root/change-evidence-packets/feature-1.md"

index="$tmpdir/artifact-index.json"
"$workspace/scripts/write-v2-artifact-index.sh" "$artifact_root" "$index"
"$workspace/scripts/verify-v2-artifact-index.sh" "$artifact_root" "$index"

printf '\nstale-negative-case\n' >> "$artifact_root/change-evidence-packets/feature-1.md"
if "$workspace/scripts/verify-v2-artifact-index.sh" "$artifact_root" "$index" >/dev/null 2>&1; then
  echo "stale-artifact-digest case unexpectedly passed" >&2
  exit 1
fi
