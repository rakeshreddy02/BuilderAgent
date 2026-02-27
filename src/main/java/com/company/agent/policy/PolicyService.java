package com.company.agent.policy;

import com.company.agent.common.Determinism;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PolicyService {

    private final Map<String, PolicyModels.Policy> policies = new ConcurrentHashMap<>();

    public PolicyModels.Policy createPolicy(PolicyModels.Policy policyRequest) {
        if (policyRequest.tenantId() == null || policyRequest.tenantId().isBlank()) throw new IllegalArgumentException("tenantId required");
        String idPayload = policyRequest.tenantId() + "|" + policyRequest.name() + "|" + policyRequest.expression();
        PolicyModels.Policy policy = new PolicyModels.Policy(
                Determinism.stableId("pol", idPayload),
                policyRequest.tenantId(),
                policyRequest.name(),
                policyRequest.expression(),
                policyRequest.enabled()
        );
        policies.put(policy.policyId(), policy);
        return policy;
    }

    public List<PolicyModels.Policy> listPolicies() {
        return policies.values().stream().toList();
    }

    public PolicyModels.PolicyDecision evaluate(String tenantId, String action) {
        List<String> violations = new ArrayList<>();
        for (PolicyModels.Policy policy : policies.values()) {
            if (policy.enabled() && policy.tenantId().equals(tenantId)
                    && policy.expression().startsWith("DENY:")
                    && action.startsWith(policy.expression().substring(5))) {
                violations.add("Denied by policy " + policy.name());
            }
        }
        return new PolicyModels.PolicyDecision(violations.isEmpty(), violations);
    }
}
