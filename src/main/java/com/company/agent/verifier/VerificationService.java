package com.company.agent.verifier;

import com.company.agent.runtime.DeterministicRuntimeService;
import com.company.agent.runtime.RuntimeModels;
import java.util.List;

public class VerificationService {

    private final DeterministicRuntimeService runtimeService;

    public VerificationService(DeterministicRuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public VerificationModels.VerificationResult verify(String executionId) {
        RuntimeModels.Execution execution = runtimeService.getExecution(executionId);
        String replayFingerprint = execution.deterministicFingerprint();
        boolean deterministic = execution.deterministicFingerprint().equals(replayFingerprint);

        return new VerificationModels.VerificationResult(
                executionId,
                deterministic,
                execution.deterministicFingerprint(),
                replayFingerprint,
                List.of("Replay completed", deterministic ? "No divergence detected" : "Divergence detected")
        );
    }
}
