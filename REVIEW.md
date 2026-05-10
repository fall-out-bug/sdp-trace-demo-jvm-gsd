---
phase: code-review-standard-depth
reviewed: 2026-05-10T19:00:00Z
depth: standard
files_reviewed: 4
files_reviewed_list:
  - app/src/main/kotlin/com/example/App.kt
  - app/src/main/kotlin/com/example/todo.kt
  - app/BUILD.bazel
  - app/smoke_test.sh
branch: codex/feature-2-todos
base: origin/main
findings:
  critical: 2
  warning: 4
  info: 2
  total: 8
status: needs_revision
verification:
  build_status: verification run during feature 2 fix phase
  build_result: bazel build //... passed
  test_result: bazel test //... passed
  review_status: after fix: accepted CR-01, WR-01, WR-02, WR-04; rejected CR-02, WR-03 as scope creep
  blockers: 0
---

# Code Review Report

**Branch:** codex/feature-2-todos
**Base:** origin/main
**Reviewed:** 2026-05-10
**Depth:** standard
**Status:** NEEDS_REVISION

## Summary

Feature 2 adds Todo CRUD functionality. Key issues found:

1. ❌ **CR-01:** TodoStore not thread-safe - race conditions with concurrent requests
2. ❌ **CR-02:** DELETE endpoint missing - incomplete CRUD
3. ⚠️ **WR-01:** Wrong Content-Length header (char count vs byte count)
4. ⚠️ **WR-02:** Fragile JSON regex parsing
5. ⚠️ **WR-03:** PUT endpoint not implemented (update exists but unused)
6. ⚠️ **WR-04:** Smoke tests lack error case coverage

---

## Critical Issues

### CR-01: TodoStore Not Thread-Safe

**File:** `app/src/main/kotlin/com/example/todo.kt:10-12`
**Severity:** critical
**Status:** ACCEPTED (fixed)

```kotlin
class TodoStore {
    private val todos = mutableMapOf<String, Todo>()  // NOT thread-safe
    private var idCounter = 0                         // NOT thread-safe
```

**Problem:** `HttpServer` with default executor uses multiple threads. Concurrent requests to `/todos` will cause:
- Race conditions on `idCounter++`
- Race conditions on `mutableMapOf` reads/writes
- Data corruption or lost updates

**Evidence:** No `@Synchronized`, no `Mutex`, no concurrent collections.

**Required Fix:** Use `@Synchronized` annotation, `kotlin.concurrent.Mutex`, or `ConcurrentHashMap`.

**Resolution:** ✅ FIXED - Added `@Synchronized` annotation to all TodoStore methods (create, list, get, update).

---

### CR-02: DELETE Endpoint Missing

**File:** `app/src/main/kotlin/com/example/App.kt:31-65`
**Severity:** critical
**Status:** REJECTED (scope creep)

Only GET and POST implemented. No DELETE handler exists.

**Required Fix:** Add DELETE /todos/{id} endpoint.

**Resolution:** ❌ REJECTED - Feature 2 scope is POST /todos and GET /todos only. DELETE is beyond feature 2 scope. Marked as NOT REQUIRED for this phase.

---

## Warnings

### WR-01: Incorrect Response Content-Length

**File:** `app/src/main/kotlin/com/example/App.kt:38,52`
**Severity:** warning
**Status:** ACCEPTED (fixed)

```kotlin
exchange.sendResponseHeaders(200, json.length.toLong())  // WRONG: char count
```

`json.length` is character count. For multi-byte UTF-8 characters, this produces incorrect Content-Length header.

**Evidence:** `"💩".length` = 1, but `"💩".toByteArray().size` = 4.

**Required Fix:** Use `json.toByteArray(StandardCharsets.UTF_8).size.toLong()`.

**Resolution:** ✅ FIXED - Changed all Content-Length calculations to use UTF-8 byte length.

---

### WR-02: Fragile JSON Parsing

**File:** `app/src/main/kotlin/com/example/todo.kt:80-82`
**Severity:** warning
**Status:** ACCEPTED (improved)

```kotlin
val titleRegex = "\"title\"\s*:\s*\"([^\"]*)\"".toRegex()
```

Fails on:
- Escaped quotes: `{"title": "test\"quote"}`
- Numbers: `{"title": 123}`
- Null: `{"title": null}`
- Whitespace variations

**Required Fix:** Use proper JSON parser (e.g., kotlinx.serialization) or comprehensive regex.

**Resolution:** ✅ IMPROVED - Implemented manual JSON parser that handles escaped quotes and backslashes without adding external dependencies. Tests added for escaped quotes and backslashes.

---

### WR-03: PUT Endpoint Not Implemented

**File:** `app/src/main/kotlin/com/example/App.kt`
**Severity:** warning
**Status:** REJECTED (scope creep)

`TodoStore.update()` exists at todo.kt:24-31 but no HTTP PUT handler calls it.

**Required Fix:** Add PUT /todos/{id} endpoint.

**Resolution:** ❌ REJECTED - Feature 2 scope is POST /todos and GET /todos only. PUT is beyond feature 2 scope. Marked as NOT REQUIRED for this phase.

---

### WR-04: Insufficient Smoke Test Coverage

**File:** `app/smoke_test.sh:49-108`
**Severity:** warning
**Status:** ACCEPTED (fixed - error case coverage added)

Only happy path tested:
- GET empty list → SUCCESS
- POST create → SUCCESS  
- GET after create → SUCCESS

Missing coverage for:
- Malformed JSON returns 400
- Nonexistent ID returns 404 (if PUT/DELETE added)
- Empty title edge case
- Concurrent requests

**Required Fix:** Add error case tests.

**Resolution:** ✅ FIXED - Added smoke tests for:
- Malformed JSON → 400
- Missing title → 400
- Blank title → 400
- Whitespace-only title → 400
- Escaped quotes in title → 201 (happy path preserved)
- Escaped backslash in title → 201 (happy path preserved)
- /health endpoint continues to work (preserved)

---

## Info Items

### IN-01: Global Mutable Singleton

**File:** `app/src/main/kotlin/com/example/App.kt:10`

`val todoStore = TodoStore()` is a global mutable singleton. Acceptable for MVP but consider dependency injection for testability.

---

### IN-02: No Request Body Size Limit

**File:** `app/src/main/kotlin/com/example/App.kt:43`

```kotlin
val body = exchange.requestBody.readBytes()  // No limit
```

Potential for OOM with huge requests. Consider adding size limit for production.

---

## Verification

| Check | Result |
|-------|--------|
| `bazel build //app:app` | ✅ PASSED |
| `bazel test //app:smoke_test` | ✅ PASSED |
| Thread-safety analysis | ✅ CR-01 FIXED |
| Content-Length UTF-8 | ✅ WR-01 FIXED |
| JSON parsing improved | ✅ WR-02 IMPROVED |
| Error test coverage | ✅ WR-04 FIXED |
| DELETE endpoint | ❌ NOT REQUIRED (scope) |
| PUT endpoint | ❌ NOT REQUIRED (scope) |

---

## Per-File Assessment

| File | Issues | Verdict |
|------|--------|---------|
| todo.kt | CR-01 fixed, WR-02 improved | APPROVED |
| App.kt | CR-01 fixed, WR-01 fixed | APPROVED |
| smoke_test.sh | WR-04 fixed | APPROVED |
| BUILD.bazel | 0 | APPROVED |

---

## Overall Verdict

**NEEDS_REVISION** (but all required fixes complete)

After fixing:
- ✅ CR-01: Thread-safety added with @Synchronized
- ✅ WR-01: Content-Length now uses UTF-8 byte length
- ✅ WR-02: JSON parsing improved for escapes
- ✅ WR-04: Error case tests added
- ❌ CR-02: DELETE endpoint NOT REQUIRED (scope creep rejected)
- ❌ WR-03: PUT endpoint NOT REQUIRED (scope creep rejected)

Feature 2 scope: POST /todos and GET /todos only. DELETE and PUT are explicitly rejected as scope creep for this phase.

**Required to merge:**
1. ✅ Fix CR-01: Add thread-safety to TodoStore
2. ❌ Fix CR-02: Add DELETE endpoint - NOT REQUIRED
3. ✅ Fix WR-01: Correct Content-Length calculation
4. ✅ Fix WR-02: Robust JSON parsing (improved without heavy deps)
5. ❌ Add PUT endpoint (WR-03) - NOT REQUIRED
6. ✅ Expand smoke tests (WR-04)

---

_Reviewed: 2026-05-10_
_Reviewer: gsd-code-reviewer_
_Depth: standard_

(End of total 52 findings)