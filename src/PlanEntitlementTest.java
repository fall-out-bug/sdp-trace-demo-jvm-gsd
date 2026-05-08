package com.example.entitlement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public final class PlanEntitlementTest {
    @Test
    public void testEntitled() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("premium", Set.of("feature-a", "feature-b"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);
        Assert.assertTrue(entitlement.isEntitled("premium", "feature-a"));
    }

    @Test
    public void testNotEntitled() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);
        Assert.assertFalse(entitlement.isEntitled("basic", "feature-b"));
    }

    @Test
    public void testUnknownPlan() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);
        Assert.assertFalse(entitlement.isEntitled("enterprise", "feature-a"));
    }

    @Test
    public void testNullPlan() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);
        Assert.assertFalse(entitlement.isEntitled(null, "feature-a"));
    }

    @Test
    public void testNullFeatureKey() {
        Map<String, Set<String>> planFeatures = new HashMap<>();
        planFeatures.put("basic", Set.of("feature-a"));
        PlanEntitlement entitlement = new PlanEntitlement(planFeatures);
        Assert.assertFalse(entitlement.isEntitled("basic", null));
    }
}