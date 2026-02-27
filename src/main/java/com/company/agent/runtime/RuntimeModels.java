package com.company.agent.runtime;

import java.time.Instant;
import java.util.List;

public final class RuntimeModels {

    private RuntimeModels() {
    }

    public record StartExecutionRequest(String tenantId, String compiledPlanId, String inputDigest) {
    }

    public record ExecutionStepResult(
            String stepId,
            String action,
            String status,
            String outputDigest,
            Instant executedAt
    ) {
    }

    public record Execution(
            String executionId,
            String tenantId,
            String compiledPlanId,
            String status,
            String deterministicFingerprint,
            List<ExecutionStepResult> steps
    ) {
    }
}
