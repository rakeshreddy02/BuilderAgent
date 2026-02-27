package com.company.agent.compiler;

import java.util.List;

public final class CompilerModels {

    private CompilerModels() {
    }

    public record CompiledStep(
            String id,
            String action,
            String inputSchema,
            String outputSchema,
            int order
    ) {
    }

    public record CompiledPlan(
            String compiledPlanId,
            String planId,
            String graphHash,
            List<CompiledStep> steps,
            String compileStatus
    ) {
    }
}
