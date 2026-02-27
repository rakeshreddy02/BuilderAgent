package com.company.agent.dsl;

import com.company.agent.common.Determinism;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DslService {

    private final Map<String, PlanModels.ParsedPlan> plans = new ConcurrentHashMap<>();

    public PlanModels.ParsedPlan parse(PlanModels.ParsePlanRequest request) {
        validate(request);
        String canonical = request.tenantId() + "|" + request.name() + "|" + request.steps();
        String planId = Determinism.stableId("plan", canonical);
        PlanModels.ParsedPlan plan = new PlanModels.ParsedPlan(
                planId,
                request.tenantId(),
                request.name(),
                request.steps(),
                Determinism.sha256(canonical),
                1
        );
        plans.put(planId, plan);
        return plan;
    }

    public PlanModels.ParsedPlan getPlan(String planId) {
        PlanModels.ParsedPlan plan = plans.get(planId);
        if (plan == null) {
            throw new IllegalArgumentException("Unknown planId: " + planId);
        }
        return plan;
    }

    private void validate(PlanModels.ParsePlanRequest request) {
        if (request.tenantId() == null || request.tenantId().isBlank()) throw new IllegalArgumentException("tenantId is required");
        if (request.name() == null || request.name().isBlank()) throw new IllegalArgumentException("name is required");
        if (request.steps() == null || request.steps().isEmpty()) throw new IllegalArgumentException("steps are required");
    }
}
