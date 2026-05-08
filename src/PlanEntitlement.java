package com.example.entitlement;

import java.util.Map;
import java.util.Set;

public final class PlanEntitlement {
    private final Map<String, Set<String>> planFeatures;

    public PlanEntitlement(Map<String, Set<String>> planFeatures) {
        this.planFeatures = Map.copyOf(planFeatures);
    }

    public boolean isEntitled(String plan, String featureKey) {
        if (plan == null || featureKey == null) {
            return false;
        }
        Set<String> features = planFeatures.get(plan);
        if (features == null) {
            return false;
        }
        return features.contains(featureKey);
    }
}