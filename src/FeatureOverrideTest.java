package com.example.entitlement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public final class FeatureOverrideTest {
    @Test
    public void testExplicitAllowOverridesPlanDeny() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);

        Map<String, Map<String, FeatureOverride.Override>> userOverrides = new HashMap<>();
        Map<String, FeatureOverride.Override> overrides = new HashMap<>();
        overrides.put("feature-b", FeatureOverride.Override.ALLOW);
        userOverrides.put("user-1", overrides);

        FeatureOverride featureOverride = new FeatureOverride(entitlement, userOverrides);
        Assert.assertTrue(featureOverride.isAllowed("user-1", "basic", "feature-b"));
    }

    @Test
    public void testExplicitDenyOverridesPlanAllow() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a", "feature-b"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);

        Map<String, Map<String, FeatureOverride.Override>> userOverrides = new HashMap<>();
        Map<String, FeatureOverride.Override> overrides = new HashMap<>();
        overrides.put("feature-b", FeatureOverride.Override.DENY);
        userOverrides.put("user-1", overrides);

        FeatureOverride featureOverride = new FeatureOverride(entitlement, userOverrides);
        Assert.assertFalse(featureOverride.isAllowed("user-1", "premium", "feature-b"));
    }

    @Test
    public void testNoOverrideFallsBackToPlanEntitlement() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);

        Map<String, Map<String, FeatureOverride.Override>> userOverrides = new HashMap<>();

        FeatureOverride featureOverride = new FeatureOverride(entitlement, userOverrides);
        Assert.assertTrue(featureOverride.isAllowed("user-1", "premium", "feature-a"));
    }

    @Test
    public void testNoOverrideFallsBackToPlanDenial() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);

        Map<String, Map<String, FeatureOverride.Override>> userOverrides = new HashMap<>();

        FeatureOverride featureOverride = new FeatureOverride(entitlement, userOverrides);
        Assert.assertFalse(featureOverride.isAllowed("user-1", "basic", "feature-b"));
    }

    @Test
    public void testExplicitNoneFallsBackToPlanEntitlement() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);

        Map<String, Map<String, FeatureOverride.Override>> userOverrides = new HashMap<>();
        Map<String, FeatureOverride.Override> overrides = new HashMap<>();
        overrides.put("feature-a", FeatureOverride.Override.NONE);
        userOverrides.put("user-1", overrides);

        FeatureOverride featureOverride = new FeatureOverride(entitlement, userOverrides);
        Assert.assertTrue(featureOverride.isAllowed("user-1", "premium", "feature-a"));
    }

    @Test
    public void testOverrideMapMutationAfterConstructionDoesNotChangeDecision() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);

        Map<String, Map<String, FeatureOverride.Override>> userOverrides = new HashMap<>();
        Map<String, FeatureOverride.Override> overrides = new HashMap<>();
        overrides.put("feature-b", FeatureOverride.Override.ALLOW);
        userOverrides.put("user-1", overrides);

        FeatureOverride featureOverride = new FeatureOverride(entitlement, userOverrides);
        overrides.put("feature-b", FeatureOverride.Override.DENY);

        Assert.assertTrue(featureOverride.isAllowed("user-1", "basic", "feature-b"));
    }

    @Test
    public void testDifferentUserNoOverride() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);

        Map<String, Map<String, FeatureOverride.Override>> userOverrides = new HashMap<>();
        Map<String, FeatureOverride.Override> overrides = new HashMap<>();
        overrides.put("feature-a", FeatureOverride.Override.ALLOW);
        userOverrides.put("user-1", overrides);

        FeatureOverride featureOverride = new FeatureOverride(entitlement, userOverrides);
        Assert.assertTrue(featureOverride.isAllowed("user-2", "basic", "feature-a"));
    }

    @Test
    public void testNullUserId() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);

        FeatureOverride featureOverride = new FeatureOverride(entitlement, new HashMap<>());
        Assert.assertFalse(featureOverride.isAllowed(null, "basic", "feature-a"));
    }

    @Test
    public void testNullPlan() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);

        FeatureOverride featureOverride = new FeatureOverride(entitlement, new HashMap<>());
        Assert.assertFalse(featureOverride.isAllowed("user-1", null, "feature-a"));
    }

    @Test
    public void testNullFeatureKey() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);

        FeatureOverride featureOverride = new FeatureOverride(entitlement, new HashMap<>());
        Assert.assertFalse(featureOverride.isAllowed("user-1", "basic", null));
    }
}
