package com.example.entitlement;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public final class ExpiredEntitlementDenyTest {
    @Test
    public void testActiveEntitlementBeforeExpiryAllowed() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);
        Instant expiry = Instant.parse("2025-01-01T00:00:00Z");
        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ExpiredEntitlementDeny deny = new ExpiredEntitlementDeny(planEntitlement, expiry, clock);
        Assert.assertTrue(deny.isAllowed("premium", "feature-a"));
    }

    @Test
    public void testActiveEntitlementAtExpiryBoundaryAllowed() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);
        Instant expiry = Instant.parse("2025-01-01T00:00:00Z");
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ExpiredEntitlementDeny deny = new ExpiredEntitlementDeny(planEntitlement, expiry, clock);
        Assert.assertTrue(deny.isAllowed("premium", "feature-a"));
    }

    @Test
    public void testExpiredEntitlementDeniedEvenIfPlanAllows() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);
        Instant expiry = Instant.parse("2025-01-01T00:00:00Z");
        Instant now = Instant.parse("2025-01-02T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ExpiredEntitlementDeny deny = new ExpiredEntitlementDeny(planEntitlement, expiry, clock);
        Assert.assertFalse(deny.isAllowed("premium", "feature-a"));
    }

    @Test
    public void testActiveEntitlementNotEntitledByPlan() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-b"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);
        Instant expiry = Instant.parse("2025-01-01T00:00:00Z");
        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ExpiredEntitlementDeny deny = new ExpiredEntitlementDeny(planEntitlement, expiry, clock);
        Assert.assertFalse(deny.isAllowed("premium", "feature-a"));
    }

    @Test
    public void testNullPlan() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);
        Instant expiry = Instant.parse("2025-01-01T00:00:00Z");
        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ExpiredEntitlementDeny deny = new ExpiredEntitlementDeny(planEntitlement, expiry, clock);
        Assert.assertFalse(deny.isAllowed(null, "feature-a"));
    }

    @Test
    public void testNullFeatureKey() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);
        Instant expiry = Instant.parse("2025-01-01T00:00:00Z");
        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ExpiredEntitlementDeny deny = new ExpiredEntitlementDeny(planEntitlement, expiry, clock);
        Assert.assertFalse(deny.isAllowed("premium", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPlanEntitlement() {
        Instant expiry = Instant.parse("2025-01-01T00:00:00Z");
        new ExpiredEntitlementDeny(null, expiry);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullExpiry() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);
        new ExpiredEntitlementDeny(planEntitlement, null);
    }
}