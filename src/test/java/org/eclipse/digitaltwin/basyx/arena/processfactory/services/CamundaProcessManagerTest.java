package org.eclipse.digitaltwin.basyx.arena.processfactory.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.digitaltwin.basyx.arena.processfactory.config.CamundaConfiguration;
import org.eclipse.digitaltwin.basyx.arena.processfactory.config.CamundaSettings;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;

/**
 * Requires running instance of Zeebe
 */
class CamundaProcessManagerTest {
    final static String TEST_BPMN_FILENAME = "fusebox.bpmn";
    final static String ZEEBE_GATEWAY = "http://localhost:26500";

    final static CamundaSettings settings = new CamundaSettings("/tmp", ZEEBE_GATEWAY);
    final static ZeebeClient zeebeClient = new CamundaConfiguration().getZeebeClient(settings);

    @Test
    void deployMostRecentProcess() throws IOException {
        CamundaProcessManager processManager = new CamundaProcessManager(settings, zeebeClient);
        processManager.addProcess(getResourceFromClasspath(TEST_BPMN_FILENAME), TEST_BPMN_FILENAME);

        DeploymentEvent deploymentEvent = processManager.deployMostRecentProcess().get();

        assertThat(deploymentEvent).isNotNull();
        assertThat(deploymentEvent.getProcesses()).isNotEmpty();
        assertThat(deploymentEvent.getProcesses()
                .stream()
                .filter(process -> process.getResourceName().contains(TEST_BPMN_FILENAME))
                .findFirst())
                .isPresent();
    }

    static InputStream getResourceFromClasspath(String relPath) throws IOException {
        return new ClassPathResource(relPath).getInputStream();
    }

}
