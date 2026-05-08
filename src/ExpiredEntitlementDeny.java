package com.example.entitlement;

import java.time.Clock;
import java.time.Instant;

public final class ExpiredEntitlementDeny {
    private final PlanEntitlement planEntitlement;
    private final Instant expiry;
    private final Clock clock;

    public ExpiredEntitlementDeny(PlanEntitlement planEntitlement, Instant expiry) {
        this(planEntitlement, expiry, Clock.systemUTC());
    }

    public ExpiredEntitlementDeny(PlanEntitlement planEntitlement, Instant expiry, Clock clock) {
        if (planEntitlement == null || expiry == null || clock == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        this.planEntitlement = planEntitlement;
        this.expiry = expiry;
        this.clock = clock;
    }

    public boolean isAllowed(String plan, String featureKey) {
        if (plan == null || featureKey == null) {
            return false;
        }
        Instant now = Instant.now(clock);
        if (now.isAfter(expiry)) {
            return false;
        }
        return planEntitlement.isEntitled(plan, featureKey);
    }

    Instant getExpiry() {
        return expiry;
    }

    Clock getClock() {
        return clock;
    }
}