#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -ne 2 ]; then
  echo "usage: verify-v2-artifact-index.sh <artifact-root> <index-json>" >&2
  exit 2
fi

artifact_root=$1
index_json=$2

if [ ! -d "$artifact_root" ]; then
  echo "artifact root is not a directory: $artifact_root" >&2
  exit 2
fi
if [ ! -f "$index_json" ]; then
  echo "index json is not a file: $index_json" >&2
  exit 2
fi

root_abs=$(cd "$artifact_root" && pwd -P)
index_abs=$(cd "$(dirname "$index_json")" && pwd -P)/$(basename "$index_json")

jq -e '.schema_version == "sdp-trace-demo-v2-artifact-index-v1"' "$index_abs" >/dev/null
jq -e '.entries | type == "array"' "$index_abs" >/dev/null

previous=
while IFS=$'\t' read -r rel want_digest want_size; do
  if [ -z "$rel" ] || [ "$rel" = "." ] || [[ "$rel" = /* ]] || [[ "$rel" = *".."* ]]; then
    echo "invalid relative path in index: $rel" >&2
    exit 1
  fi
  if [ "$rel" \< "$previous" ]; then
    echo "index entries are not sorted: $rel after $previous" >&2
    exit 1
  fi
  previous=$rel

  file_abs="$root_abs/$rel"
  if [ "$(cd "$(dirname "$file_abs")" && pwd -P)/$(basename "$file_abs")" = "$index_abs" ]; then
    echo "index self-entry is forbidden: $rel" >&2
    exit 1
  fi
  if [ ! -f "$file_abs" ]; then
    echo "indexed file missing: $rel" >&2
    exit 1
  fi

  got_digest=$(sha256sum "$file_abs" | awk '{print $1}')
  got_size=$(wc -c < "$file_abs" | tr -d ' ')
  if [ "$got_digest" != "$want_digest" ]; then
    echo "digest mismatch for $rel" >&2
    exit 1
  fi
  if [ "$got_size" != "$want_size" ]; then
    echo "size mismatch for $rel" >&2
    exit 1
  fi
done < <(jq -r '.entries[] | [.path, .sha256, (.size_bytes|tostring)] | @tsv' "$index_abs")
