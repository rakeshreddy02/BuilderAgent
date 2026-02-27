# DEL-AI (Deterministic Execution Layer for AI)

This repository contains an MVP implementation of a **Deterministic Execution Layer for AI** with package boundaries aligned to enterprise architecture:

- `com.company.agent.dsl` — typed plan input model + parser/validator
- `com.company.agent.compiler` — plan-to-compiled-graph transformation
- `com.company.agent.policy` — policy registration + deny-rule evaluation
- `com.company.agent.runtime` — deterministic execution runtime
- `com.company.agent.verifier` — replay/verification result model
- `com.company.agent.adapters` — deterministic adapter abstraction

## What is implemented

- Typed execution-plan model with canonical hashing.
- Stable deterministic IDs for plans, compiled plans, and executions.
- Deterministic runtime outputs using seeded digests.
- Policy engine with `DENY:<prefix>` rules.
- Verification service that reports replay consistency.
- End-to-end runnable demo (`DelAiApplication`).
- End-to-end deterministic tests via a no-dependency Java test harness.

## Run

```bash
javac -d out $(find src/main/java -name '*.java')
java -cp out com.company.agent.DelAiApplication
```

## Test

```bash
javac -d out $(find src/main/java src/test/java -name '*.java')
java -cp out com.company.agent.DeterministicRuntimeTest
```

## Next enterprise steps

- Replace in-memory stores with PostgreSQL + event sourcing.
- Add Kafka contracts and schema-registry validation.
- Add OAuth2/OIDC RBAC enforcement.
- Add immutable audit records + OpenTelemetry spans.
