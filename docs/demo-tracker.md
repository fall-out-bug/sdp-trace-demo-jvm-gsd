# Demo Tracker

| item | issue | PR | CI | packet | review | theater | decision |
| --- | --- | --- | --- | --- | --- | --- | --- |
| v1 baseline | n/a | n/a | not_assessed | n/a | not_assessed | n/a | n/a |
| packetization setup | not_assessed | not_assessed | cannot_verify | not_assessed | partial | not_assessed | owner state: not_assessed |
| selected feature packet | not_assessed | not_assessed | cannot_verify | pass | in_progress | not_assessed | owner state: not_assessed |
| feature 1 | not_assessed | not_assessed | cannot_verify | pass | in_progress | not_assessed | owner state: not_assessed |
| feature 2 | not_assessed | not_assessed | not_assessed | not_assessed | not_assessed | not_assessed | owner state: not_assessed |
| feature 3 | not_assessed | not_assessed | not_assessed | not_assessed | not_assessed | not_assessed | owner state: not_assessed |
| feature 4 | not_assessed | not_assessed | not_assessed | not_assessed | not_assessed | not_assessed | owner state: not_assessed |
| feature 5 | not_assessed | not_assessed | not_assessed | not_assessed | not_assessed | not_assessed | owner state: not_assessed |
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
