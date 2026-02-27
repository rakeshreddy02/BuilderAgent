package com.company.agent.compiler;

import com.company.agent.common.Determinism;
import com.company.agent.dsl.DslService;
import com.company.agent.dsl.PlanModels;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlanCompilerService {

    private final DslService dslService;
    private final Map<String, CompilerModels.CompiledPlan> compiledPlans = new ConcurrentHashMap<>();

    public PlanCompilerService(DslService dslService) {
        this.dslService = dslService;
    }

    public CompilerModels.CompiledPlan compile(String planId) {
        PlanModels.ParsedPlan parsedPlan = dslService.getPlan(planId);
        List<CompilerModels.CompiledStep> steps = new ArrayList<>();
        for (int i = 0; i < parsedPlan.steps().size(); i++) {
            PlanModels.PlanStepInput step = parsedPlan.steps().get(i);
            steps.add(new CompilerModels.CompiledStep(step.id(), step.action(), step.inputSchema(), step.outputSchema(), i));
        }
        String graphHash = Determinism.sha256(parsedPlan.schemaHash() + "|" + steps);
        String compiledPlanId = Determinism.stableId("cplan", planId + "|" + graphHash);
        CompilerModels.CompiledPlan compiledPlan = new CompilerModels.CompiledPlan(compiledPlanId, planId, graphHash, steps, "COMPILED");
        compiledPlans.put(compiledPlanId, compiledPlan);
        return compiledPlan;
    }

    public CompilerModels.CompiledPlan getCompiledPlan(String compiledPlanId) {
        CompilerModels.CompiledPlan compiledPlan = compiledPlans.get(compiledPlanId);
        if (compiledPlan == null) throw new IllegalArgumentException("Unknown compiledPlanId: " + compiledPlanId);
        return compiledPlan;
    }
}
