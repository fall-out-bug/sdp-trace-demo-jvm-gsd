#!/bin/bash
set -euo pipefail

if [[ -n "${TEST_SRCDIR:-}" && -n "${TEST_WORKSPACE:-}" ]]; then
    FEATURES_JSON="$TEST_SRCDIR/$TEST_WORKSPACE/gsd/features.json"
else
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
    FEATURES_JSON="$REPO_ROOT/gsd/features.json"
fi

if [[ ! -f "$FEATURES_JSON" ]]; then
    echo "ERROR: $FEATURES_JSON not found"
    exit 1
fi

FEATURE_COUNT=$(jq '.features | length' "$FEATURES_JSON")

if [[ "$FEATURE_COUNT" -ne 5 ]]; then
    echo "ERROR: Expected 5 features, found $FEATURE_COUNT"
    exit 1
fi

ALLOWED_STATUSES=("planned" "implemented_on_branch")

echo "Validating feature records..."

jq -r '.features[] | "\(.id)|\(.status)"' "$FEATURES_JSON" | while IFS='|' read -r id status; do
    echo "  Checking feature: $id (status: $status)"

    if [[ ! " ${ALLOWED_STATUSES[*]} " =~ " $status " ]]; then
        echo "ERROR: Invalid status '$status' for feature '$id'"
        exit 1
    fi

    if [[ "$status" == "implemented_on_branch" ]]; then
        if [[ ! -d "evidence/$id" ]]; then
            echo "ERROR: Missing evidence/$id for feature '$id'"
            exit 1
        fi
        if [[ ! -d "trace/$id" ]]; then
            echo "ERROR: Missing trace/$id for feature '$id'"
            exit 1
        fi
        if [[ ! -d "provenance/$id" ]]; then
            echo "ERROR: Missing provenance/$id for feature '$id'"
            exit 1
        fi
    fi
done

echo "Bootstrap check passed: 5 feature records validated"
exit 0