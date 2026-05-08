package com.example.entitlement;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public final class ManualRiskOverrideTest {
    @Test
    public void testApprovedNonExpiredOverrideWithApproverAndReasonAllows() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of());
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "approver-1", "reason-1",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        Map<String, ManualRiskOverride.OverrideRecord> userOverride = new HashMap<>();
        userOverride.put("feature-a", record);
        overrides.put("user-1", userOverride);

        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        Assert.assertTrue(override.isAllowed("user-1", "basic", "feature-a"));
    }

    @Test
    public void testExpiredOverrideDeniedFallsBackToPlanEntitlement() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "approver-1", "reason-1",
            Instant.parse("2024-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        Map<String, ManualRiskOverride.OverrideRecord> userOverride = new HashMap<>();
        userOverride.put("feature-a", record);
        overrides.put("user-1", userOverride);

        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        Assert.assertTrue(override.isAllowed("user-1", "premium", "feature-a"));
    }

    @Test
    public void testUnapprovedOverrideDeniedFallsBackToPlanEntitlement() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", false, "approver-1", "reason-1",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        Map<String, ManualRiskOverride.OverrideRecord> userOverride = new HashMap<>();
        userOverride.put("feature-a", record);
        overrides.put("user-1", userOverride);

        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        Assert.assertTrue(override.isAllowed("user-1", "premium", "feature-a"));
    }

    @Test
    public void testEmptyApproverDeniedFallsBackToPlanEntitlement() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "", "reason-1",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        Map<String, ManualRiskOverride.OverrideRecord> userOverride = new HashMap<>();
        userOverride.put("feature-a", record);
        overrides.put("user-1", userOverride);

        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        Assert.assertTrue(override.isAllowed("user-1", "premium", "feature-a"));
    }

    @Test
    public void testBlankApproverDoesNotGrantManualOverride() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of());
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "   ", "reason-1",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        overrides.put("user-1", Map.of("feature-a", record));

        Clock clock = Clock.fixed(Instant.parse("2024-06-01T00:00:00Z"), ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        Assert.assertFalse(override.isAllowed("user-1", "basic", "feature-a"));
    }

    @Test
    public void testEmptyReasonDeniedFallsBackToPlanEntitlement() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "approver-1", "",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        Map<String, ManualRiskOverride.OverrideRecord> userOverride = new HashMap<>();
        userOverride.put("feature-a", record);
        overrides.put("user-1", userOverride);

        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        Assert.assertTrue(override.isAllowed("user-1", "premium", "feature-a"));
    }

    @Test
    public void testBlankReasonDoesNotGrantManualOverride() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of());
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "approver-1", "   ",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        overrides.put("user-1", Map.of("feature-a", record));

        Clock clock = Clock.fixed(Instant.parse("2024-06-01T00:00:00Z"), ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        Assert.assertFalse(override.isAllowed("user-1", "basic", "feature-a"));
    }

    @Test
    public void testMismatchedRecordScopeDoesNotGrantManualOverride() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of());
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-2", "feature-b", true, "approver-1", "reason-1",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        overrides.put("user-1", Map.of("feature-a", record));

        Clock clock = Clock.fixed(Instant.parse("2024-06-01T00:00:00Z"), ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        Assert.assertFalse(override.isAllowed("user-1", "basic", "feature-a"));
    }

    @Test
    public void testNullExpiryDoesNotGrantManualOverride() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of());
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "approver-1", "reason-1", null
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        overrides.put("user-1", Map.of("feature-a", record));

        Clock clock = Clock.fixed(Instant.parse("2024-06-01T00:00:00Z"), ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        Assert.assertFalse(override.isAllowed("user-1", "basic", "feature-a"));
    }

    @Test
    public void testNoOverrideFallsBackToPlanEntitlement() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, new HashMap<>());

        Assert.assertTrue(override.isAllowed("user-1", "premium", "feature-a"));
    }

    @Test
    public void testNoOverrideFallsBackToPlanDenial() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-b"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, new HashMap<>());

        Assert.assertFalse(override.isAllowed("user-1", "basic", "feature-a"));
    }

    @Test
    public void testDifferentUserNoOverride() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "approver-1", "reason-1",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        Map<String, ManualRiskOverride.OverrideRecord> userOverride = new HashMap<>();
        userOverride.put("feature-a", record);
        overrides.put("user-1", userOverride);

        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        Assert.assertTrue(override.isAllowed("user-2", "basic", "feature-a"));
    }

    @Test
    public void testDifferentFeatureNoOverride() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "approver-1", "reason-1",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        Map<String, ManualRiskOverride.OverrideRecord> userOverride = new HashMap<>();
        userOverride.put("feature-a", record);
        overrides.put("user-1", userOverride);

        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        Assert.assertFalse(override.isAllowed("user-1", "basic", "feature-b"));
    }

    @Test
    public void testNullUserId() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, new HashMap<>());

        Assert.assertFalse(override.isAllowed(null, "premium", "feature-a"));
    }

    @Test
    public void testNullPlan() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, new HashMap<>());

        Assert.assertFalse(override.isAllowed("user-1", null, "feature-a"));
    }

    @Test
    public void testNullFeatureKey() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, new HashMap<>());

        Assert.assertFalse(override.isAllowed("user-1", "premium", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPlanEntitlement() {
        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "approver-1", "reason-1",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        new ManualRiskOverride(null, Map.of("user-1", Map.of("feature-a", record)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullClock() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "approver-1", "reason-1",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        overrides.put("user-1", Map.of("feature-a", record));

        new ManualRiskOverride(planEntitlement, overrides, null);
    }

    @Test
    public void testOverrideMapMutationAfterConstructionDoesNotChangeDecision() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of());
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "approver-1", "reason-1",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        Map<String, ManualRiskOverride.OverrideRecord> userOverride = new HashMap<>();
        userOverride.put("feature-a", record);
        overrides.put("user-1", userOverride);

        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        userOverride.put("feature-a", new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", false, "", "",
            Instant.parse("2025-01-01T00:00:00Z")
        ));

        Assert.assertTrue(override.isAllowed("user-1", "basic", "feature-a"));
    }

    @Test
    public void testAtExpiryBoundaryAllowed() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of());
        PlanEntitlement planEntitlement = new PlanEntitlement(planFeatures);

        ManualRiskOverride.OverrideRecord record = new ManualRiskOverride.OverrideRecord(
            "user-1", "feature-a", true, "approver-1", "reason-1",
            Instant.parse("2025-01-01T00:00:00Z")
        );
        Map<String, Map<String, ManualRiskOverride.OverrideRecord>> overrides = new HashMap<>();
        Map<String, ManualRiskOverride.OverrideRecord> userOverride = new HashMap<>();
        userOverride.put("feature-a", record);
        overrides.put("user-1", userOverride);

        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        ManualRiskOverride override = new ManualRiskOverride(planEntitlement, overrides, clock);

        Assert.assertTrue(override.isAllowed("user-1", "basic", "feature-a"));
    }
}
