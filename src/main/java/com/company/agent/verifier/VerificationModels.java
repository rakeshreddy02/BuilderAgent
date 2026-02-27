package com.company.agent.verifier;

import java.util.List;

public final class VerificationModels {

    private VerificationModels() {
    }

    public record VerificationResult(
            String executionId,
            boolean deterministic,
            String originalFingerprint,
            String replayFingerprint,
            List<String> notes
    ) {
    }
}
