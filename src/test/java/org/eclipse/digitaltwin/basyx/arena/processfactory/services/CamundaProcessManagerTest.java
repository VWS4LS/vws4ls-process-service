package org.eclipse.digitaltwin.basyx.arena.processfactory.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.digitaltwin.basyx.arena.processfactory.config.CamundaSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest;
import io.camunda.zeebe.process.test.inspections.InspectionUtility;

@ZeebeProcessTest
class CamundaProcessManagerTest {
    final static String TEST_BPMN_FILENAME = "fusebox.bpmn";

    final static CamundaSettings settings = new CamundaSettings("/tmp", null);

    @Autowired
    private ZeebeClient zeebeClient;

    @Test
    void deployMostRecentProcess() throws IOException {
        CamundaProcessManager processManager = new CamundaProcessManager(settings, zeebeClient);
        processManager.addProcessResource(getResourceFromClasspath(TEST_BPMN_FILENAME), TEST_BPMN_FILENAME);

        DeploymentEvent deploymentEvent = processManager.deployMostRecentProcess().join();

        assertThat(deploymentEvent).isNotNull();
        assertThat(deploymentEvent.getProcesses()).isNotEmpty();
        assertThat(deploymentEvent.getProcesses()
                .stream()
                .filter(process -> process.getResourceName().contains(TEST_BPMN_FILENAME))
                .findFirst())
                .isPresent();

        processManager.createProcessInstance(deploymentEvent).join();

        BpmnAssert.assertThat(InspectionUtility.findProcessInstances().findFirstProcessInstance().get());
    }

    static InputStream getResourceFromClasspath(String relPath) throws IOException {
        return new ClassPathResource(relPath).getInputStream();
    }

}
