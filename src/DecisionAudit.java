package com.example.entitlement;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public final class DecisionAudit {
    public static final class Entry {
        private final String userId;
        private final String featureKey;
        private final boolean allowed;
        private final String reason;

        public Entry(String userId, String featureKey, boolean allowed, String reason) {
            this.userId = userId;
            this.featureKey = featureKey;
            this.allowed = allowed;
            this.reason = reason;
        }

        public String getUserId() {
            return userId;
        }

        public String getFeatureKey() {
            return featureKey;
        }

        public boolean isAllowed() {
            return allowed;
        }

        public String getReason() {
            return reason;
        }
    }

    private final List<Entry> entries;

    public DecisionAudit() {
        this.entries = new ArrayList<>();
    }

    public void record(String userId, String featureKey, boolean allowed, String reason) {
        entries.add(new Entry(userId, featureKey, allowed, reason));
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(new ArrayList<>(entries));
    }
}