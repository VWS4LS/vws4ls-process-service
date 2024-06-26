package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import io.camunda.zeebe.client.ZeebeClient;

/**
 * 
 * 
 * @author mateusmolina
 */
@Service
public class ZeebeWorkerManager {

    private final ZeebeClient zeebeClient;

    private final Deque<Long> workerIds = new ArrayDeque<>();

    public ZeebeWorkerManager(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    public CompletableFuture<Long> deployWorker() {
        throw new RuntimeException("todo");
    }

    public CompletableFuture<?> abortWorker(long workerId) {
        throw new RuntimeException("todo");
    }
}
