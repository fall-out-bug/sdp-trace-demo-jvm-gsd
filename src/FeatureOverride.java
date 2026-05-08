package com.example.entitlement;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class FeatureOverride {
    public enum Override {
        ALLOW,
        DENY,
        NONE
    }

    private final PlanEntitlement planEntitlement;
    private final Map<String, Map<String, Override>> userOverrides;

    public FeatureOverride(PlanEntitlement planEntitlement, Map<String, Map<String, Override>> userOverrides) {
        this.planEntitlement = planEntitlement;
        this.userOverrides = userOverrides.entrySet().stream()
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> Map.copyOf(entry.getValue())
            ));
    }

    public boolean isAllowed(String userId, String plan, String featureKey) {
        if (userId == null || plan == null || featureKey == null) {
            return false;
        }
        Map<String, Override> userOverrideMap = userOverrides.get(userId);
        if (userOverrideMap != null) {
            Override override = userOverrideMap.get(featureKey);
            if (override == Override.ALLOW) {
                return true;
            }
            if (override == Override.DENY) {
                return false;
            }
        }
        return planEntitlement.isEntitled(plan, featureKey);
    }
}
