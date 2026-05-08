package com.entitlement

class EntitlementValidatorTest {

    private fun assertTrue(actual: Boolean, message: String = "Expected true but was false") {
        if (!actual) throw AssertionError(message)
    }

    private fun assertFalse(actual: Boolean, message: String = "Expected false but was true") {
        if (actual) throw AssertionError(message)
    }

    private fun <T> assertEquals(expected: T, actual: T, message: String = "Values not equal") {
        if (expected != actual) throw AssertionError("$message: expected $expected but got $actual")
    }

    private fun assertNotNull(actual: Any?, message: String = "Expected non-null but was null") {
        if (actual == null) throw AssertionError(message)
    }

    // ===== Role-based validation tests =====

    fun testRoleBasedAllow() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-admin",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin", "user"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.ALLOW, response.decision)
        assertTrue(response.matchedRuleIds.contains("rule-allow-admin"))
    }

    fun testRoleBasedDeny() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-deny-admin",
                decision = EntitlementDecision.DENY,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin", "user"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.DENY, response.decision)
    }

    fun testRoleNotMatched() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-admin",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("user"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.DENY, response.decision)
    }

    // ===== Group-based validation tests =====

    fun testGroupBasedAllow() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-devops",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(GroupCondition("devops"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = emptyList(),
            groups = listOf("devops", "users"),
            customAttributes = emptyMap()
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.ALLOW, response.decision)
    }

    fun testGroupBasedDeny() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-deny-contractor",
                decision = EntitlementDecision.DENY,
                conditions = listOf(GroupCondition("contractor"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = emptyList(),
            groups = listOf("contractor"),
            customAttributes = emptyMap()
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.DENY, response.decision)
    }

    fun testGroupNotMatched() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-devops",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(GroupCondition("devops"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = emptyList(),
            groups = listOf("users"),
            customAttributes = emptyMap()
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.DENY, response.decision)
    }

    // ===== Custom attribute validation tests =====

    fun testCustomAttributeAllow() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-tier1",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(CustomAttributeCondition("tier", "1"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = emptyList(),
            groups = emptyList(),
            customAttributes = mapOf("tier" to "1", "department" to "engineering")
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.ALLOW, response.decision)
    }

    fun testCustomAttributeDeny() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-deny-tier3",
                decision = EntitlementDecision.DENY,
                conditions = listOf(CustomAttributeCondition("tier", "3"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = emptyList(),
            groups = emptyList(),
            customAttributes = mapOf("tier" to "3")
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.DENY, response.decision)
    }

    fun testCustomAttributeNotMatched() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-tier1",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(CustomAttributeCondition("tier", "1"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = emptyList(),
            groups = emptyList(),
            customAttributes = mapOf("tier" to "2")
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.DENY, response.decision)
    }

    // ===== AND semantics tests =====

    fun testAndSemanticsAllMatch() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-admin-devops",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(
                    RoleCondition("admin"),
                    GroupCondition("devops")
                )
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin"),
            groups = listOf("devops"),
            customAttributes = emptyMap()
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.ALLOW, response.decision)
        assertTrue(response.matchedRuleIds.contains("rule-admin-devops"))
    }

    fun testAndSemanticsOneFails() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-admin-devops",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(
                    RoleCondition("admin"),
                    GroupCondition("devops")
                )
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.DENY, response.decision)
    }

    fun testAndSemanticsWithCustomAttribute() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-admin-tier1",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(
                    RoleCondition("admin"),
                    CustomAttributeCondition("tier", "1")
                )
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin"),
            groups = emptyList(),
            customAttributes = mapOf("tier" to "1")
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.ALLOW, response.decision)
    }

    // ===== List order evaluation tests =====

    fun testFirstMatchWins() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-deny-first",
                decision = EntitlementDecision.DENY,
                conditions = listOf(RoleCondition("admin"))
            )
        )
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-second",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.DENY, response.decision)
        assertTrue(response.matchedRuleIds.contains("rule-deny-first"))
    }

    fun testLaterRulesEvaluated() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-deny-all",
                decision = EntitlementDecision.DENY,
                conditions = listOf(RoleCondition("nonexistent"))
            )
        )
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-admin",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.ALLOW, response.decision)
        assertEquals(listOf("rule-allow-admin"), response.matchedRuleIds)
    }

    // ===== Rule configuration tests =====

    fun testAddRule() {
        val validator = EntitlementValidator()
        assertEquals(0, validator.getRules().size)

        validator.addRule(
            EntitlementRule(
                ruleId = "rule1",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        assertEquals(1, validator.getRules().size)
    }

    fun testAddRuleChaining() {
        val validator = EntitlementValidator()
        validator
            .addRule(EntitlementRule("r1", EntitlementDecision.ALLOW, listOf(RoleCondition("a"))))
            .addRule(EntitlementRule("r2", EntitlementDecision.DENY, listOf(RoleCondition("b"))))

        assertEquals(2, validator.getRules().size)
    }

    fun testRemoveRule() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule1",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        assertEquals(1, validator.getRules().size)

        validator.removeRule("rule1")

        assertEquals(0, validator.getRules().size)
    }

    fun testRemoveRuleNotFound() {
        val validator = EntitlementValidator()
        validator.removeRule("nonexistent")
        assertEquals(0, validator.getRules().size)
    }

    fun testClearRules() {
        val validator = EntitlementValidator()
        validator.addRule(EntitlementRule("r1", EntitlementDecision.ALLOW, listOf(RoleCondition("a"))))
        validator.clearRules()

        assertEquals(0, validator.getRules().size)
    }

    // ===== Audit record tests =====

    fun testAuditRecordCreated() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-admin",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        validator.validate(request)

        val auditLog = validator.getAuditLog()
        val records = auditLog.getByUserId("user123")

        assertEquals(1, records.size)
        val record = records[0]
        assertEquals("user123", record.userId)
        assertEquals("feature-x", record.entitlementId)
    }

    fun testAuditRecordContainsEvaluatedRules() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-admin",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-deny-user",
                decision = EntitlementDecision.DENY,
                conditions = listOf(RoleCondition("user"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        validator.validate(request)

        val auditLog = validator.getAuditLog()
        val records = auditLog.getByUserId("user123")

        assertTrue(records[0].evaluatedRuleIds.size >= 1)
    }

    fun testAuditRecordMatchedRule() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-admin",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        validator.validate(request)

        val auditLog = validator.getAuditLog()
        val records = auditLog.getByUserId("user123")

        assertEquals(EntitlementDecision.ALLOW, records[0].decision)
    }

    fun testAuditQueryByUserId() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-admin",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        validator.validate(EntitlementRequest("user1", "e1", listOf("admin"), emptyList(), emptyMap()))
        validator.validate(EntitlementRequest("user2", "e1", listOf("admin"), emptyList(), emptyMap()))
        validator.validate(EntitlementRequest("user1", "e2", listOf("admin"), emptyList(), emptyMap()))

        val auditLog = validator.getAuditLog()
        val records = auditLog.getByUserId("user1")

        assertEquals(2, records.size)
    }

    // ===== Runtime modification tests =====

    fun testRuntimeAddRule() {
        val validator = EntitlementValidator()

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        var response = validator.validate(request)
        assertEquals(EntitlementDecision.DENY, response.decision)

        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-admin",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        response = validator.validate(request)
        assertEquals(EntitlementDecision.ALLOW, response.decision)
    }

    fun testRuntimeRemoveRule() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-admin",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        var response = validator.validate(request)
        assertEquals(EntitlementDecision.ALLOW, response.decision)

        validator.removeRule("rule-allow-admin")

        response = validator.validate(request)
        assertEquals(EntitlementDecision.DENY, response.decision)
    }

    // ===== Default behavior tests =====

    fun testDefaultDenyWhenNoRules() {
        val validator = EntitlementValidator()

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("admin"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.DENY, response.decision)
    }

    fun testDefaultDenyWhenNoMatchingRule() {
        val validator = EntitlementValidator()
        validator.addRule(
            EntitlementRule(
                ruleId = "rule-allow-admin",
                decision = EntitlementDecision.ALLOW,
                conditions = listOf(RoleCondition("admin"))
            )
        )

        val request = EntitlementRequest(
            userId = "user123",
            entitlementId = "feature-x",
            roles = listOf("user"),
            groups = emptyList(),
            customAttributes = emptyMap()
        )

        val response = validator.validate(request)

        assertEquals(EntitlementDecision.DENY, response.decision)
    }

    fun runAllTests() {
        println("Running EntitlementValidator tests...")

        var passed = 0
        var failed = 0

        val tests = listOf(
            "testRoleBasedAllow" to { testRoleBasedAllow(); passed++ },
            "testRoleBasedDeny" to { testRoleBasedDeny(); passed++ },
            "testRoleNotMatched" to { testRoleNotMatched(); passed++ },
            "testGroupBasedAllow" to { testGroupBasedAllow(); passed++ },
            "testGroupBasedDeny" to { testGroupBasedDeny(); passed++ },
            "testGroupNotMatched" to { testGroupNotMatched(); passed++ },
            "testCustomAttributeAllow" to { testCustomAttributeAllow(); passed++ },
            "testCustomAttributeDeny" to { testCustomAttributeDeny(); passed++ },
            "testCustomAttributeNotMatched" to { testCustomAttributeNotMatched(); passed++ },
            "testAndSemanticsAllMatch" to { testAndSemanticsAllMatch(); passed++ },
            "testAndSemanticsOneFails" to { testAndSemanticsOneFails(); passed++ },
            "testAndSemanticsWithCustomAttribute" to { testAndSemanticsWithCustomAttribute(); passed++ },
            "testFirstMatchWins" to { testFirstMatchWins(); passed++ },
            "testLaterRulesEvaluated" to { testLaterRulesEvaluated(); passed++ },
            "testAddRule" to { testAddRule(); passed++ },
            "testAddRuleChaining" to { testAddRuleChaining(); passed++ },
            "testRemoveRule" to { testRemoveRule(); passed++ },
            "testRemoveRuleNotFound" to { testRemoveRuleNotFound(); passed++ },
            "testClearRules" to { testClearRules(); passed++ },
            "testAuditRecordCreated" to { testAuditRecordCreated(); passed++ },
            "testAuditRecordContainsEvaluatedRules" to { testAuditRecordContainsEvaluatedRules(); passed++ },
            "testAuditRecordMatchedRule" to { testAuditRecordMatchedRule(); passed++ },
            "testAuditQueryByUserId" to { testAuditQueryByUserId(); passed++ },
            "testRuntimeAddRule" to { testRuntimeAddRule(); passed++ },
            "testRuntimeRemoveRule" to { testRuntimeRemoveRule(); passed++ },
            "testDefaultDenyWhenNoRules" to { testDefaultDenyWhenNoRules(); passed++ },
            "testDefaultDenyWhenNoMatchingRule" to { testDefaultDenyWhenNoMatchingRule(); passed++ }
        )

        for ((name, test) in tests) {
            try {
                test()
                println("  ✓ $name")
            } catch (e: Throwable) {
                println("  ✗ $name: ${e.message}")
                failed++
            }
        }

        println("\nResults: $passed passed, $failed failed")
        if (failed > 0) throw AssertionError("$failed tests failed")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            EntitlementValidatorTest().runAllTests()
        }
    }
}
