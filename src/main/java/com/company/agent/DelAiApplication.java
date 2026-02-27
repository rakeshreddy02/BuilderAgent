package com.company.agent;

import com.company.agent.compiler.CompilerModels;
import com.company.agent.compiler.PlanCompilerService;
import com.company.agent.dsl.DslService;
import com.company.agent.dsl.PlanModels;
import com.company.agent.policy.PolicyService;
import com.company.agent.runtime.DeterministicRuntimeService;
import com.company.agent.runtime.RuntimeModels;
import com.company.agent.verifier.VerificationService;
import java.util.List;

public class DelAiApplication {

    public static void main(String[] args) {
        DslService dslService = new DslService();
        PlanCompilerService compiler = new PlanCompilerService(dslService);
        PolicyService policyService = new PolicyService();
        DeterministicRuntimeService runtime = new DeterministicRuntimeService(compiler, policyService);
        VerificationService verificationService = new VerificationService(runtime);

        PlanModels.ParsedPlan plan = dslService.parse(new PlanModels.ParsePlanRequest(
                "tenant-a",
                "sample",
                List.of(new PlanModels.PlanStepInput("s1", "PAYMENT.AUTH", "input.v1", "output.v1"))
        ));

        CompilerModels.CompiledPlan compiledPlan = compiler.compile(plan.planId());
        RuntimeModels.Execution execution = runtime.start(new RuntimeModels.StartExecutionRequest(
                "tenant-a", compiledPlan.compiledPlanId(), "input-digest"
        ));

        System.out.println("Execution: " + execution.executionId() + " status=" + execution.status());
        System.out.println("Verification deterministic=" + verificationService.verify(execution.executionId()).deterministic());
    }
}
