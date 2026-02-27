package com.company.agent.adapters;

import com.company.agent.common.Determinism;

public class AdapterGateway {

    public String executeDeterministically(String action, String seed) {
        return Determinism.sha256(action + "|" + seed).substring(0, 24);
    }
}
