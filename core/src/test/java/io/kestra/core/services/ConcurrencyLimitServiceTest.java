package io.kestra.core.services;

import io.kestra.core.junit.annotations.ExecuteFlow;
import io.kestra.core.junit.annotations.KestraTest;
import io.kestra.core.junit.annotations.LoadFlows;
import io.kestra.core.models.executions.Execution;
import io.kestra.core.models.flows.State;
import io.kestra.core.queues.QueueException;
import io.kestra.core.runners.ConcurrencyLimit;
import io.kestra.core.runners.TestRunnerUtils;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

@KestraTest(startRunner = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConcurrencyLimitServiceTest {
    private static final String TESTS_FLOW_NS = "io.kestra.tests";
    private static final String TENANT_ID = "main";

    @Inject
    private TestRunnerUtils runnerUtils;

    @Inject
    private ConcurrencyLimitService concurrencyLimitService;

    @AfterEach
    void tearDown() {
        concurrencyLimitService.find(TENANT_ID)
            .forEach(limit -> concurrencyLimitService.update(limit.withRunning(0)));
    }

    @Test
    @LoadFlows("flows/valids/flow-concurrency-queue.yml")
    void unqueueExecution() throws QueueException, TimeoutException {
        // run a first flow so the second is queued
        runnerUtils.runOneUntilRunning(TENANT_ID, TESTS_FLOW_NS, "flow-concurrency-queue");

        Execution result = runnerUtils.runOneUntil(TENANT_ID, TESTS_FLOW_NS, "flow-concurrency-queue", execution -> execution.getState().isQueued());
        assertThat(result.getState().isQueued()).isTrue();

        Execution unqueued = concurrencyLimitService.unqueue(result, State.Type.RUNNING);
        assertThat(unqueued.getState().isRunning()).isTrue();
    }

    @Test
    @ExecuteFlow("flows/valids/flow-concurrency-queue.yml")
    void findById(Execution execution) {
        Optional<ConcurrencyLimit> limit = concurrencyLimitService.findById(execution.getTenantId(), execution.getNamespace(), execution.getFlowId());

        assertThat(limit).isNotEmpty();
        assertThat(limit.get().getTenantId()).isEqualTo(execution.getTenantId());
        assertThat(limit.get().getNamespace()).isEqualTo(execution.getNamespace());
        assertThat(limit.get().getFlowId()).isEqualTo(execution.getFlowId());
        assertThat(limit.get().getRunning()).isEqualTo(0);
    }

    @Test
    @ExecuteFlow("flows/valids/flow-concurrency-queue.yml")
    void update(Execution execution) {
        Optional<ConcurrencyLimit> limit = concurrencyLimitService.findById(execution.getTenantId(), execution.getNamespace(), execution.getFlowId());

        assertThat(limit).isNotEmpty();
        ConcurrencyLimit updated =  limit.get().withRunning(99);
        concurrencyLimitService.update(updated);


        limit = concurrencyLimitService.findById(execution.getTenantId(), execution.getNamespace(), execution.getFlowId());
        assertThat(limit).isNotEmpty();
        assertThat(limit.get().getRunning()).isEqualTo(99);
    }

    @Test
    @ExecuteFlow("flows/valids/flow-concurrency-queue.yml")
    void list(Execution execution) {
        List<ConcurrencyLimit> list = concurrencyLimitService.find(execution.getTenantId());

        assertThat(list).isNotEmpty();
        assertThat(list.getFirst().getTenantId()).isEqualTo(execution.getTenantId());
        assertThat(list.getFirst().getNamespace()).isEqualTo(execution.getNamespace());
        assertThat(list.getFirst().getFlowId()).isEqualTo(execution.getFlowId());
    }
}