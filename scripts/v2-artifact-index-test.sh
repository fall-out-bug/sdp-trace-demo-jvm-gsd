#!/usr/bin/env bash
set -euo pipefail

workspace=${BUILD_WORKSPACE_DIRECTORY:-$(pwd)}
tmpdir=$(mktemp -d)
trap 'rm -rf "$tmpdir"' EXIT

artifact_root="$tmpdir/downloaded-artifacts"
mkdir -p "$artifact_root/change-evidence-packets" "$artifact_root/evidence-bundles/feature-1"
cp "$workspace/.sdp-trace/packets/feature-1.md" "$artifact_root/change-evidence-packets/feature-1.md"
cp "$workspace/.sdp-trace/bundles/feature-1/bundle.json" "$artifact_root/evidence-bundles/feature-1/bundle.json"

index_a="$tmpdir/artifact-index-a.json"
index_b="$tmpdir/artifact-index-b.json"

"$workspace/scripts/write-v2-artifact-index.sh" "$artifact_root" "$index_a"
"$workspace/scripts/write-v2-artifact-index.sh" "$artifact_root" "$index_b"

cmp "$index_a" "$index_b"
"$workspace/scripts/verify-v2-artifact-index.sh" "$artifact_root" "$index_a"

if jq -e '.entries[].path | select(. == "artifact-index-a.json")' "$index_a" >/dev/null; then
  echo "index must not include itself" >&2
  exit 1
fi

printf '\nmutated\n' >> "$artifact_root/change-evidence-packets/feature-1.md"
if "$workspace/scripts/verify-v2-artifact-index.sh" "$artifact_root" "$index_a" >/dev/null 2>&1; then
  echo "verification unexpectedly passed after artifact mutation" >&2
  exit 1
fi
