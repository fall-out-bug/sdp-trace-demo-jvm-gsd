# Demo Tracker

| item | issue | PR | CI | packet | review | theater | decision |
| --- | --- | --- | --- | --- | --- | --- | --- |
| v1 baseline | n/a | n/a | not_assessed | n/a | not_assessed | n/a | n/a |
| packetization setup | not_assessed | not_assessed | cannot_verify | not_assessed | partial | not_assessed | owner state: not_assessed |
| selected feature packet | not_assessed | not_assessed | cannot_verify | pass | in_progress | not_assessed | owner state: not_assessed |
| feature 1 | not_assessed | not_assessed | cannot_verify | pass | in_progress | not_assessed | owner state: not_assessed |
| feature 2 | not_assessed | not_assessed | cannot_verify | pass | pass | not_assessed | owner state: not_assessed |
| feature 3 | not_assessed | not_assessed | cannot_verify | pass | pass | not_assessed | owner state: not_assessed |
| feature 4 | not_assessed | not_assessed | cannot_verify | pass | pass | not_assessed | owner state: not_assessed |
| feature 5 | not_assessed | not_assessed | cannot_verify | not_assessed | pass | not_assessed | owner state: not_assessed |
| negative | not_assessed | not_assessed | not_assessed | not_assessed | not_assessed | not_assessed | owner state: not_assessed |

Allowed states: `not_started`, `in_progress`, `pass`, `partial`, `fail`,
`not_assessed`, `cannot_verify`.

Setup rows do not close feature packet rows. Feature rows require retained
route, CI, packet, bundle, and review evidence.

Current setup review: `docs/setup-boundary-review.md`. Local build/test passed,
but CI remains `cannot_verify` until a PR run uploads artifacts.

Feature 1 route: `GET /ready` has local OpenCode/MiniMax route observation in
`.evidence/feature-readiness-opencode-direct-free/`, followed by an OpenCode
review-fix route in `.evidence/feature-readiness-review-fix/`. Route proof is
`partial`: the package does not claim source-bound authorship or CI witness
until the feature is committed and a PR run retains artifacts.

Feature 2 route: `GET /live` has local OpenCode/MiniMax route observation in
`.evidence/feature-liveness-opencode/`, local verification in
`.evidence/feature-liveness-final-verification/`, review disposition via
subagent thread `019e18a2-efb0-7963-ac3d-9688203c32d3`, and packet checks in
`.evidence/feature-2-packet-validate/` plus
`.evidence/feature-2-packet-check-demo/`. Route and verification proof remain
`partial`/`cannot_verify` where no source-bound PR CI witness exists yet.

Feature 3 route: `GET /version` has local GSD/OpenCode/MiniMax route
observation in `.evidence/feature-version-gsd-opencode/`, local verification
in `.evidence/feature-version-final-verification/`, and review disposition via
subagent thread `019e18c5-17da-7821-a28f-fde6a71002d5`. Route proof is
`partial`; packet checks are retained in `.evidence/feature-3-packet-validate/`
and `.evidence/feature-3-packet-check-demo/`. CI remains `cannot_verify` until
a PR run retains artifacts.

Feature 4 route: `GET /ping` has local OpenCode/MiniMax route observation in
`.evidence/feature-ping-opencode/`, GSD/OpenCode/MiniMax review-repair route in
`.evidence/feature-ping-gsd-review-repair/`, local verification in
`.evidence/feature-ping-final-verification/`, and review disposition via
subagent thread `019e18c8-df18-7512-9316-1f6a587bb86d`. Route proof is
`partial`; observed OpenCode tool-schema friction is retained in the feature
route manifest. Packet checks are retained in
`.evidence/feature-4-packet-validate/` and
`.evidence/feature-4-packet-check-demo/`. CI remains `cannot_verify` until a PR
run retains artifacts.

Feature 5 route: `GET /info` has local GSD/OpenCode/MiniMax route observation
in `.evidence/feature-info-gsd-opencode/`, local verification in
`.evidence/feature-info-final-verification/`, and review disposition via
subagent thread `019e18ce-3105-7272-98db-34c27dc9b381`. Route proof is
`partial`; CI remains `cannot_verify` until a PR run retains artifacts.
