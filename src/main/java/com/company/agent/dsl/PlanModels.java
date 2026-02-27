package com.company.agent.dsl;

import java.util.List;

public final class PlanModels {

    private PlanModels() {
    }

    public record ParsePlanRequest(String tenantId, String name, List<PlanStepInput> steps) {
    }

    public record PlanStepInput(String id, String action, String inputSchema, String outputSchema) {
    }

    public record ParsedPlan(
            String planId,
            String tenantId,
            String name,
            List<PlanStepInput> steps,
            String schemaHash,
            int version
    ) {
    }
}
