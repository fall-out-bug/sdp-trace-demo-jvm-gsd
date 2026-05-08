# GSD Ledger

This directory contains the Governance/Scope/Definition (GSD) ledger for the SDP Trace Demo JVM GSD project.

## Structure

- `features.json` - Feature registry with IDs and status tracking
- `pr-ledger.md` - PR history and feature delivery log

## Feature Statuses

- `planned` - Feature approved and scheduled
- `implemented_on_branch` - Feature delivered via PR

## Artifacts

Each feature with `implemented_on_branch` status produces:
- `evidence/<id>/` - Evidence of implementation
- `trace/<id>/` - Trace artifacts
- `provenance/<id>/` - Provenance data