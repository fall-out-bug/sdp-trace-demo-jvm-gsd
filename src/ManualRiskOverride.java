package com.example.entitlement;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ManualRiskOverride {
    public static final class OverrideRecord {
        private final String userId;
        private final String featureKey;
        private final boolean approved;
        private final String approver;
        private final String reason;
        private final Instant expiry;

        public OverrideRecord(String userId, String featureKey, boolean approved,
                           String approver, String reason, Instant expiry) {
            this.userId = userId;
            this.featureKey = featureKey;
            this.approved = approved;
            this.approver = approver;
            this.reason = reason;
            this.expiry = expiry;
        }

        public String getUserId() {
            return userId;
        }

        public String getFeatureKey() {
            return featureKey;
        }

        public boolean isApproved() {
            return approved;
        }

        public String getApprover() {
            return approver;
        }

        public String getReason() {
            return reason;
        }

        public Instant getExpiry() {
            return expiry;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OverrideRecord that = (OverrideRecord) o;
            return approved == that.approved &&
                   Objects.equals(userId, that.userId) &&
                   Objects.equals(featureKey, that.featureKey) &&
                   Objects.equals(approver, that.approver) &&
                   Objects.equals(reason, that.reason) &&
                   Objects.equals(expiry, that.expiry);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, featureKey, approved, approver, reason, expiry);
        }
    }

    private final PlanEntitlement planEntitlement;
    private final Map<String, Map<String, OverrideRecord>> overrides;
    private final Clock clock;

    public ManualRiskOverride(PlanEntitlement planEntitlement, Map<String, Map<String, OverrideRecord>> overrides) {
        this(planEntitlement, overrides, Clock.systemUTC());
    }

    public ManualRiskOverride(PlanEntitlement planEntitlement, Map<String, Map<String, OverrideRecord>> overrides, Clock clock) {
        if (planEntitlement == null || clock == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        this.planEntitlement = planEntitlement;
        this.overrides = overrides == null ? Map.of() : overrides.entrySet().stream()
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> Map.copyOf(entry.getValue())
            ));
        this.clock = clock;
    }

    public boolean isAllowed(String userId, String plan, String featureKey) {
        if (userId == null || featureKey == null || plan == null) {
            return false;
        }
        OverrideRecord override = getOverride(userId, featureKey);
        if (override != null && isValidOverride(override, userId, featureKey)) {
            return true;
        }
        return planEntitlement.isEntitled(plan, featureKey);
    }

    private OverrideRecord getOverride(String userId, String featureKey) {
        Map<String, OverrideRecord> userOverrideMap = overrides.get(userId);
        if (userOverrideMap == null) {
            return null;
        }
        return userOverrideMap.get(featureKey);
    }

    private boolean isValidOverride(OverrideRecord record, String userId, String featureKey) {
        if (!Objects.equals(record.getUserId(), userId) || !Objects.equals(record.getFeatureKey(), featureKey)) {
            return false;
        }
        if (!record.isApproved()) {
            return false;
        }
        if (record.getExpiry() == null) {
            return false;
        }
        Instant now = Instant.now(clock);
        if (now.isAfter(record.getExpiry())) {
            return false;
        }
        String approver = record.getApprover();
        if (approver == null || approver.trim().isEmpty()) {
            return false;
        }
        String reason = record.getReason();
        if (reason == null || reason.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    Clock getClock() {
        return clock;
    }
}
