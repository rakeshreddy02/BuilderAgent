package com.company.agent.runtime;

import com.company.agent.adapters.AdapterGateway;
import com.company.agent.common.Determinism;
import com.company.agent.compiler.CompilerModels;
import com.company.agent.compiler.PlanCompilerService;
import com.company.agent.policy.PolicyModels;
import com.company.agent.policy.PolicyService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeterministicRuntimeService {

    private final PlanCompilerService compilerService;
    private final PolicyService policyService;
    private final AdapterGateway adapterGateway;
    private final Map<String, RuntimeModels.Execution> executions = new ConcurrentHashMap<>();

    public DeterministicRuntimeService(PlanCompilerService compilerService, PolicyService policyService) {
        this.compilerService = compilerService;
        this.policyService = policyService;
        this.adapterGateway = new AdapterGateway();
    }

    public RuntimeModels.Execution start(RuntimeModels.StartExecutionRequest request) {
        CompilerModels.CompiledPlan compiledPlan = compilerService.getCompiledPlan(request.compiledPlanId());
        List<RuntimeModels.ExecutionStepResult> stepResults = new ArrayList<>();
        String seed = request.tenantId() + "|" + compiledPlan.graphHash() + "|" + request.inputDigest();
        String executionId = Determinism.stableId("exec", seed);

        for (CompilerModels.CompiledStep step : compiledPlan.steps()) {
            PolicyModels.PolicyDecision decision = policyService.evaluate(request.tenantId(), step.action());
            if (!decision.allowed()) {
                RuntimeModels.Execution denied = new RuntimeModels.Execution(
                        executionId,
                        request.tenantId(),
                        request.compiledPlanId(),
                        "DENIED",
                        Determinism.sha256(seed),
                        stepResults
                );
                executions.put(executionId, denied);
                return denied;
            }
            String out = adapterGateway.executeDeterministically(step.action(), seed + "|" + step.order());
            stepResults.add(new RuntimeModels.ExecutionStepResult(step.id(), step.action(), "SUCCEEDED", out, Instant.now()));
        }

        RuntimeModels.Execution execution = new RuntimeModels.Execution(
                executionId,
                request.tenantId(),
                request.compiledPlanId(),
                "SUCCEEDED",
                Determinism.sha256(seed),
                stepResults
        );
        executions.put(executionId, execution);
        return execution;
    }

    public RuntimeModels.Execution getExecution(String executionId) {
        RuntimeModels.Execution execution = executions.get(executionId);
        if (execution == null) throw new IllegalArgumentException("Unknown executionId: " + executionId);
        return execution;
    }
}
