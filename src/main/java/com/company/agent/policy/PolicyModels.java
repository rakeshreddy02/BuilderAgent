package com.company.agent.policy;

import java.util.List;

public final class PolicyModels {

    private PolicyModels() {
    }

    public record Policy(String policyId, String tenantId, String name, String expression, boolean enabled) {
    }

    public record PolicyDecision(boolean allowed, List<String> reasons) {
    }
}
