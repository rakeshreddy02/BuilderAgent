package com.company.agent;

import com.company.agent.compiler.CompilerModels;
import com.company.agent.compiler.PlanCompilerService;
import com.company.agent.dsl.DslService;
import com.company.agent.dsl.PlanModels;
import com.company.agent.policy.PolicyService;
import com.company.agent.runtime.DeterministicRuntimeService;
import com.company.agent.runtime.RuntimeModels;
import com.company.agent.verifier.VerificationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DelAiApplication {

    public static void main(String[] args) throws IOException {
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

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> writeResponse(exchange, execution, verificationService));
        server.start();
        System.out.println("Web status available at http://localhost:8080");
    }

    private static void writeResponse(
            HttpExchange exchange,
            RuntimeModels.Execution execution,
            VerificationService verificationService
    ) throws IOException {
        String body = """
                <html>
                  <head><title>DEL-AI Runtime</title></head>
                  <body>
                    <h1>DEL-AI Runtime</h1>
                    <p>Execution: %s</p>
                    <p>Status: %s</p>
                    <p>Deterministic verification: %s</p>
                  </body>
                </html>
                """.formatted(
                execution.executionId(),
                execution.status(),
                verificationService.verify(execution.executionId()).deterministic()
        );

        byte[] responseBytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
        }
    }
}
