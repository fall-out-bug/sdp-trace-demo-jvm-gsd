# DEMO-NEGATIVE: Agent-Claimed Verification

This draft PR is a controlled negative demo. It intentionally retains an agent
claim that verification passed without an independent GitHub Actions artifact or
signed witness.

Expected packet behavior:

- `PC-VERIFICATION`: `cannot_verify`
- `PC-THEATER`: triggered `agent_claimed_verification`
- merge/release decisions: not approved

This demonstrates packet validation of a supplied theater finding. It does not
claim automated theater detection beyond the current packet validator behavior.
