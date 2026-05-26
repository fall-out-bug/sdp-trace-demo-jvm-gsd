#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -ne 2 ]; then
  echo "usage: write-v2-artifact-index.sh <artifact-root> <out-index>" >&2
  exit 2
fi

artifact_root=$1
out_index=$2

if [ ! -d "$artifact_root" ]; then
  echo "artifact root is not a directory: $artifact_root" >&2
  exit 2
fi

mkdir -p "$(dirname "$out_index")"

root_abs=$(cd "$artifact_root" && pwd -P)
out_dir=$(dirname "$out_index")
out_name=$(basename "$out_index")
tmp_index="$out_dir/.$out_name.tmp"

(
  cd "$root_abs"
  printf '{\n'
  printf '  "schema_version": "sdp-trace-demo-v2-artifact-index-v1",\n'
  printf '  "entries": [\n'

  first=1
  while IFS= read -r rel; do
    digest=$(sha256sum "$rel" | awk '{print $1}')
    size=$(wc -c < "$rel" | tr -d ' ')
    if [ "$first" -eq 0 ]; then
      printf ',\n'
    fi
    first=0
    printf '    {"path": %s, "sha256": "%s", "size_bytes": %s}' \
      "$(jq -Rn --arg v "$rel" '$v')" "$digest" "$size"
  done < <(find . -type f -printf '%P\n' | LC_ALL=C sort)

  printf '\n'
  printf '  ]\n'
  printf '}\n'
) > "$tmp_index"

mv "$tmp_index" "$out_index"
