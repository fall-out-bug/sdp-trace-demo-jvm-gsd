package com.entitlement

import java.time.Instant

/**
 * Request object for entitlement validation.
 * Contains user identity and attributes required for rule evaluation.
 */
data class EntitlementRequest(
    val userId: String,
    val entitlementId: String,
    val roles: List<String> = emptyList(),
    val groups: List<String> = emptyList(),
    val customAttributes: Map<String, String> = emptyMap()
)

/**
 * Response object returned from entitlement validation.
 * Contains decision, reasoning, and rule evaluation details.
 */
data class EntitlementResponse(
    val requestId: String,
    val decision: EntitlementDecision,
    val reason: String,
    val matchedRuleIds: List<String> = emptyList(),
    val evaluatedRuleIds: List<String> = emptyList()
)

/**
 * Decision result from entitlement validation.
 */
enum class EntitlementDecision {
    ALLOW,
    DENY
}

/**
 * Sealed hierarchy for entitlement rule conditions.
 * Supports role, group, and custom attribute matching.
 *
 * Per D-01: Sealed rule hierarchy for type safety
 */
sealed interface EntitlementCondition {
    /**
     * Check if this condition matches the given request.
     */
    fun matches(request: EntitlementRequest): Boolean
}

/**
 * Condition that matches when user has a specific role.
 */
data class RoleCondition(
    val roleName: String
) : EntitlementCondition {
    override fun matches(request: EntitlementRequest): Boolean {
        return request.roles.contains(roleName)
    }
}

/**
 * Condition that matches when user is in a specific group.
 */
data class GroupCondition(
    val groupName: String
) : EntitlementCondition {
    override fun matches(request: EntitlementRequest): Boolean {
        return request.groups.contains(groupName)
    }
}

/**
 * Condition that matches when user has a specific custom attribute value.
 */
data class CustomAttributeCondition(
    val attributeName: String,
    val attributeValue: String
) : EntitlementCondition {
    override fun matches(request: EntitlementRequest): Boolean {
        return request.customAttributes[attributeName] == attributeValue
    }
}

/**
 * Entitlement rule with decision and conditions.
 * Per D-01: Minimal Phase 1 - allow/deny from role, group, custom attribute
 * Per D-02: Deterministic exact matching, AND semantics
 *
 * All conditions must match for the rule to match (AND semantics).
 * Rules are evaluated in list order, first match wins.
 */
data class EntitlementRule(
    val ruleId: String,
    val decision: EntitlementDecision,
    val conditions: List<EntitlementCondition>
) {
    /**
     * Evaluate this rule against a request.
     * Returns true if ALL conditions match (AND semantics).
     */
    fun evaluate(request: EntitlementRequest): Boolean {
        return conditions.all { it.matches(request) }
    }

    /**
     * Get reason string for this rule matching.
     */
    fun getReason(): String {
        val conditionDescriptions = conditions.joinToString(" AND ") { describeCondition(it) }
        return "Rule $ruleId ($decision) when $conditionDescriptions"
    }

    private fun describeCondition(condition: EntitlementCondition): String {
        return when (condition) {
            is RoleCondition -> "role=${condition.roleName}"
            is GroupCondition -> "group=${condition.groupName}"
            is CustomAttributeCondition -> "${condition.attributeName}=${condition.attributeValue}"
        }
    }
}
