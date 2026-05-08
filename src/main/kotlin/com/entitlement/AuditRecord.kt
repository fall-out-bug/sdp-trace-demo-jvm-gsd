package com.entitlement

import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Audit record for entitlement validation decision.
 * Per D-03: Structured fields - request ID, user ID, entitlement ID, decision, matched rules, evaluated rules, reason, timestamp
 */
data class AuditRecord(
    val requestId: String,
    val userId: String,
    val entitlementId: String,
    val decision: EntitlementDecision,
    val matchedRuleIds: List<String> = emptyList(),
    val evaluatedRuleIds: List<String> = emptyList(),
    val reason: String,
    val timestamp: Instant = Instant.now()
)

/**
 * In-memory audit log for entitlement validation decisions.
 * Supports adding records and basic query operations.
 * Per ENT-04: Validation is purely in-memory
 */
class AuditLog {
    private val records = CopyOnWriteArrayList<AuditRecord>()

    /**
     * Add an audit record to the log.
     */
    fun add(record: AuditRecord) {
        records.add(record)
    }

    /**
     * Get all audit records.
     */
    fun getAll(): List<AuditRecord> {
        return records.toList()
    }

    /**
     * Get audit records for a specific user.
     */
    fun getByUserId(userId: String): List<AuditRecord> {
        return records.filter { it.userId == userId }
    }

    /**
     * Get audit records for a specific entitlement.
     */
    fun getByEntitlementId(entitlementId: String): List<AuditRecord> {
        return records.filter { it.entitlementId == entitlementId }
    }

    /**
     * Get audit records for a specific request ID.
     */
    fun getByRequestId(requestId: String): AuditRecord? {
        return records.find { it.requestId == requestId }
    }

    /**
     * Get audit records with a specific decision.
     */
    fun getByDecision(decision: EntitlementDecision): List<AuditRecord> {
        return records.filter { it.decision == decision }
    }

    /**
     * Clear all audit records.
     */
    fun clear() {
        records.clear()
    }

    /**
     * Get the count of audit records.
     */
    fun size(): Int {
        return records.size
    }
}