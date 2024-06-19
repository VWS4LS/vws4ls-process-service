package org.eclipse.digitaltwin.basyx.arena.processfactory.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

import org.eclipse.digitaltwin.basyx.arena.processfactory.config.CamundaSettings;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
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

    private final CamundaSettings settings;
    private final ZeebeClient zeebeClient;
    private Deque<String> processes = new ArrayDeque<>();

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
    public ZeebeFuture<ProcessInstanceEvent> createProcessInstance(DeploymentEvent event) {
        Process firstProcess = event.getProcesses().get(0);
        return zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(firstProcess.getBpmnProcessId())
                .latestVersion()
                .send();
    }

    /**
     * Deploys the most recent process to a Zeebe Server
     * 
     * @param filePath
     */
    public ZeebeFuture<DeploymentEvent> deployMostRecentProcess() {
        return deployProcess(processes.peek());
    }

    /**
     * Deploy a process to a Zeebe Server
     * 
     * @param filePath
     */
    public ZeebeFuture<DeploymentEvent> deployProcess(String filePath) {
        return zeebeClient.newDeployResourceCommand().addResourceFile(filePath).send();
    }

    /**
     * Adds a BPMN process to be managed
     * 
     * @param is
     * @param fileName, if null or blank, DEFAULT_FILENAME is used
     * @return filePath
     * @throws IOException
     */
    public String addProcess(InputStream is, String fileName) throws IOException {
        String folderPath = settings.managedProcessesPath();
        fileName = (fileName == null || fileName.isBlank()) ? DEFAULT_FILENAME : fileName;
        File outputFile = new File(folderPath, fileName);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            processes.add(outputFile.getAbsolutePath());

            return outputFile.getAbsolutePath();
        }
    }
}
