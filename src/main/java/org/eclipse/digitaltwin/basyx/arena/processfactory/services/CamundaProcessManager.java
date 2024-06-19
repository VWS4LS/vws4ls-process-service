package org.eclipse.digitaltwin.basyx.arena.processfactory.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import org.eclipse.digitaltwin.basyx.arena.processfactory.config.CamundaSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;

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

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CamundaSettings settings;
    private final ZeebeClient zeebeClient;
    private Deque<String> processes = new ArrayDeque<>();

    public CamundaProcessManager(CamundaSettings settings, ZeebeClient zeebeClient) {
        this.settings = settings;
        this.zeebeClient = zeebeClient;
    }

    /**
     * Deploys the most recent process to a Zeebe Server
     * 
     * @param filePath
     * @return Optional with the filePath of the deployed process
     */
    public Optional<DeploymentEvent> deployMostRecentProcess() {
        return deployProcess(processes.peek());
    }

    /**
     * Deploys a process to a Zeebe Server
     * 
     * @param filePath
     * @return Optional with the filePath of the deployed process
     */
    public Optional<DeploymentEvent> deployProcess(String filePath) {
        try {
            return Optional.of(zeebeClient.newDeployResourceCommand().addResourceFile(filePath).send().join());
        } catch (Exception e) {
            logger.error("Failed to deploy BPMN file at " + filePath, e);
            return Optional.empty();
        }
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
