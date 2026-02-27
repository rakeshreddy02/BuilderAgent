package com.company.agent;

import com.company.agent.compiler.CompilerModels;
import com.company.agent.compiler.PlanCompilerService;
import com.company.agent.dsl.DslService;
import com.company.agent.dsl.PlanModels;
import com.company.agent.policy.PolicyModels;
import com.company.agent.policy.PolicyService;
import com.company.agent.runtime.DeterministicRuntimeService;
import com.company.agent.runtime.RuntimeModels;
import java.util.List;

public class DeterministicRuntimeTest {

    public static void main(String[] args) {
        testDeterministicExecutionIdentity();
        testPolicyDenial();
        System.out.println("All deterministic tests passed");
    }

    private static void testDeterministicExecutionIdentity() {
        DslService dsl = new DslService();
        PlanCompilerService compiler = new PlanCompilerService(dsl);
        PolicyService policy = new PolicyService();
        DeterministicRuntimeService runtime = new DeterministicRuntimeService(compiler, policy);

        PlanModels.ParsedPlan parsed = dsl.parse(new PlanModels.ParsePlanRequest(
                "tenant-a",
                "settlement-plan",
                List.of(new PlanModels.PlanStepInput("s1", "PAYMENT.AUTH", "in-v1", "out-v1"))
        ));
        CompilerModels.CompiledPlan compiled = compiler.compile(parsed.planId());

        RuntimeModels.StartExecutionRequest req = new RuntimeModels.StartExecutionRequest("tenant-a", compiled.compiledPlanId(), "input-123");
        RuntimeModels.Execution e1 = runtime.start(req);
        RuntimeModels.Execution e2 = runtime.start(req);

        if (!e1.executionId().equals(e2.executionId())) throw new AssertionError("Execution IDs are not stable");
        if (!e1.deterministicFingerprint().equals(e2.deterministicFingerprint())) throw new AssertionError("Fingerprint is not stable");
    }

    private static void testPolicyDenial() {
        DslService dsl = new DslService();
        PlanCompilerService compiler = new PlanCompilerService(dsl);
        PolicyService policy = new PolicyService();
        DeterministicRuntimeService runtime = new DeterministicRuntimeService(compiler, policy);

        policy.createPolicy(new PolicyModels.Policy(null, "tenant-b", "deny payments", "DENY:PAYMENT", true));
        PlanModels.ParsedPlan parsed = dsl.parse(new PlanModels.ParsePlanRequest(
                "tenant-b",
                "blocked-plan",
                List.of(new PlanModels.PlanStepInput("s1", "PAYMENT.AUTH", "in-v1", "out-v1"))
        ));
        CompilerModels.CompiledPlan compiled = compiler.compile(parsed.planId());
        RuntimeModels.Execution execution = runtime.start(new RuntimeModels.StartExecutionRequest("tenant-b", compiled.compiledPlanId(), "ctx"));

        if (!"DENIED".equals(execution.status())) throw new AssertionError("Execution should be denied");
    }
}
