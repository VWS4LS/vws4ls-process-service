package org.eclipse.digitaltwin.basyx.arena.processfactory.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.arena.processfactory.config.CamundaSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.response.CancelProcessInstanceResponse;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.Process;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

/**
 * Manages BPMN processes in a configured system directory
 * 
 * Processes can be added and deployed to Zeebe
 * 
 * @author mateusmolina
 */
@Component
public class CamundaProcessManager {

    public static final String DEFAULT_FILENAME = "process.bpmn";

    private static final Logger logger = LoggerFactory.getLogger(CamundaProcessManager.class);

    private final CamundaSettings settings;
    private final ZeebeClient zeebeClient;
    private Deque<String> processResources = new ArrayDeque<>();
    private Deque<Long> processes = new ArrayDeque<>();

    public CamundaProcessManager(CamundaSettings settings, ZeebeClient zeebeClient) {
        this.settings = settings;
        this.zeebeClient = zeebeClient;
    }

    /**
     * Instantiates the first deployed process in a DeploymentEvent
     * 
     * @param event
     * @return
     */
    public CompletableFuture<ProcessInstanceEvent> createProcessInstance(DeploymentEvent event) {
        Process firstProcess = event.getProcesses().get(0);

        logger.info("Creating a new ProcessInstanceEvent for bpmnProcessId " + firstProcess.getBpmnProcessId());

        return zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(firstProcess.getBpmnProcessId())
                .latestVersion()
                .send()
                .whenComplete((p, t) -> processes.add(p.getProcessInstanceKey()))
                .toCompletableFuture();
    }

    public CompletableFuture<List<CancelProcessInstanceResponse>> killAllRunningProcesses() {
        logger.info("Killing all running processes");

        return allOf(processes.stream()
                .map(this::cancelProcess)
                .onClose(() -> processes.clear())
                .collect(Collectors.toList()));
    }

    /**
     * Deploys the most recent process to a Zeebe Server
     * 
     * @param filePath
     */
    public ZeebeFuture<DeploymentEvent> deployMostRecentProcess() {
        return deployProcess(processResources.peek());
    }

    /**
     * Deploy a process to a Zeebe Server
     * 
     * @param filePath
     */
    public ZeebeFuture<DeploymentEvent> deployProcess(String filePath) {
        logger.info("Creating DeploymentEvent for resouce at '" + filePath + "'");

        return zeebeClient.newDeployResourceCommand().addResourceFile(filePath).send();
    }

    /**
     * Adds a process resource to be managed
     * 
     * @param is
     * @param fileName, if null or blank, DEFAULT_FILENAME is used
     * @return filePath
     * @throws IOException
     */
    public String addProcessResource(InputStream is, String fileName) throws IOException {
        String folderPath = settings.managedProcessesPath();
        fileName = (fileName == null || fileName.isBlank()) ? DEFAULT_FILENAME : fileName;
        File outputFile = new File(folderPath, fileName);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            processResources.add(outputFile.getAbsolutePath());

            return outputFile.getAbsolutePath();
        }
    }

    private CompletableFuture<CancelProcessInstanceResponse> cancelProcess(long processInstanceKey) {
        return zeebeClient.newCancelInstanceCommand(processInstanceKey)
                .send()
                .exceptionally((t) -> {
                    logger.warn(t.getMessage());
                    return null;
                }).toCompletableFuture();
    }

    private static <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futuresList) {
        CompletableFuture<Void> allFuturesResult = CompletableFuture
                .allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));
        return allFuturesResult
                .thenApply(v -> futuresList.stream().map(CompletableFuture::join).collect(Collectors.<T>toList()));
    }
}
