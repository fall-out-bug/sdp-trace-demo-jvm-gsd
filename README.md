# SDP Trace Demo JVM GSD

JVM GSD demo proving five independent feature deliveries through feature branches, PRs, GitHub CI, and CI-uploaded provenance/evidence/trace artifacts.

## Features

- plan-entitlement
- feature-override
- decision-audit
- expired-entitlement-denial
- manual-risk-override

## Build

```bash
bazel test //...
```

## Bootstrap Check

```bash
bash scripts/bootstrap_check.sh
```