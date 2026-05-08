package com.example.entitlement;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public final class DecisionAuditTest {
    @Test
    public void testRecordEntry() {
        DecisionAudit audit = new DecisionAudit();
        audit.record("user-1", "feature-a", true, "plan-entitled");
        List<DecisionAudit.Entry> entries = audit.getEntries();
        Assert.assertEquals(1, entries.size());
    }

    @Test
    public void testRecordMultipleEntries() {
        DecisionAudit audit = new DecisionAudit();
        audit.record("user-1", "feature-a", true, "plan-entitled");
        audit.record("user-2", "feature-b", false, "not-entitled");
        List<DecisionAudit.Entry> entries = audit.getEntries();
        Assert.assertEquals(2, entries.size());
    }

    @Test
    public void testEntryData() {
        DecisionAudit audit = new DecisionAudit();
        audit.record("user-1", "feature-a", true, "plan-entitled");
        DecisionAudit.Entry entry = audit.getEntries().get(0);
        Assert.assertEquals("user-1", entry.getUserId());
        Assert.assertEquals("feature-a", entry.getFeatureKey());
        Assert.assertTrue(entry.isAllowed());
        Assert.assertEquals("plan-entitled", entry.getReason());
    }

    @Test
    public void testImmutability() {
        DecisionAudit audit = new DecisionAudit();
        audit.record("user-1", "feature-a", true, "plan-entitled");
        List<DecisionAudit.Entry> entries = audit.getEntries();
        try {
            entries.add(new DecisionAudit.Entry("user-2", "feature-b", false, "test"));
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void testEmptyAudit() {
        DecisionAudit audit = new DecisionAudit();
        Assert.assertTrue(audit.getEntries().isEmpty());
    }
}