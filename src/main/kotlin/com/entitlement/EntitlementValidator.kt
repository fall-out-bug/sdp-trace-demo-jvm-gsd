package com.entitlement

import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Entitlement validator with configurable rules and audit logging.
 * Per D-04: Direct construction, immutable config
 * Per ENT-02: User can configure entitlement rules programmatically
 * Per ENT-05: Rules can be added/removed at runtime
 */
class EntitlementValidator(
    initialRules: List<EntitlementRule> = emptyList()
) {
    // Thread-safe rule list
    private val rules = CopyOnWriteArrayList(initialRules)

    // Audit log for validation decisions
    private val auditLog = AuditLog()

    /**
     * Add a rule to the validator.
     * Per ENT-05: Rules can be added at runtime
     *
     * @return this validator for chaining
     */
    fun addRule(rule: EntitlementRule): EntitlementValidator {
        rules.add(rule)
        return this
    }

    /**
     * Remove a rule by its identifier.
     * Per ENT-05: Rules can be removed at runtime
     *
     * @return true if rule was removed, false if not found
     */
    fun removeRule(ruleId: String): Boolean {
        return rules.removeAll { it.ruleId == ruleId }
    }

    /**
     * Get all configured rules.
     */
    fun getRules(): List<EntitlementRule> {
        return rules.toList()
    }

    /**
     * Validate an entitlement request.
     * Per ENT-01: System can validate single entitlement against configured rules
     *
     * Evaluation logic per D-02:
     * - Rules are evaluated in list order
     * - First matching rule (all conditions match) wins
     * - If no rules match, default to DENY
     * - All evaluated rules are recorded for audit
     *
     * @return EntitlementResponse with decision and context
     */
    fun validate(request: EntitlementRequest): EntitlementResponse {
        val requestId = UUID.randomUUID().toString()
        val evaluatedRuleIds = mutableListOf<String>()
        var matchedRuleId: String? = null
        var decision = EntitlementDecision.DENY
        var reason = "No matching rule found - default deny"

        // Evaluate rules in list order. Stop at the first match so audit output
        // reflects the actual decision path instead of unreachable later rules.
        for (rule in rules) {
            evaluatedRuleIds.add(rule.ruleId)

            if (rule.evaluate(request)) {
                matchedRuleId = rule.ruleId
                decision = rule.decision
                reason = rule.getReason()
                break
            }
        }

        val matchedRuleIds = if (matchedRuleId != null) listOf(matchedRuleId) else emptyList()

        val response = EntitlementResponse(
            requestId = requestId,
            decision = decision,
            reason = reason,
            matchedRuleIds = matchedRuleIds,
            evaluatedRuleIds = evaluatedRuleIds
        )

        // Create and add audit record
        // Per ENT-03: Validation result includes decision audit log with context
        val auditRecord = AuditRecord(
            requestId = requestId,
            userId = request.userId,
            entitlementId = request.entitlementId,
            decision = decision,
            matchedRuleIds = matchedRuleIds,
            evaluatedRuleIds = evaluatedRuleIds,
            reason = reason
        )
        auditLog.add(auditRecord)

        return response
    }

    /**
     * Get the audit log for this validator.
     * Per ENT-03: Validation result includes decision audit log with context
     */
    fun getAuditLog(): AuditLog {
        return auditLog
    }

    /**
     * Clear all rules from the validator.
     */
    fun clearRules() {
        rules.clear()
    }

    /**
     * Get the count of configured rules.
     */
    fun ruleCount(): Int {
        return rules.size
    }
}
