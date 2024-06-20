package org.eclipse.digitaltwin.basyx.arena.processfactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.basyx.arena.processfactory.services.BasyxOperationDeployer;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest;
import io.camunda.zeebe.process.test.inspections.InspectionUtility;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;

@ZeebeProcessTest
@SpringBootTest(classes = {
        ProcessfactoryApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CompleteScenarioIT {

    static final String BASYX_OPERATIONSSMURL = "http://localhost:8081/submodels/aHR0cHM6Ly9leGFtcGxlLmNvbS9pZHMvc20vNjU5Ml85MDMyXzYwMzJfNjc2Nw%3D%3D";
    static final String MQTT_SERVERURI = "tcp://localhost:1884";
    static final String MQTT_TOPIC = "test_topic";

    static final ConnectedSubmodelService smService = new ConnectedSubmodelService(BASYX_OPERATIONSSMURL);

    static final String BPMN_RESOURCE_PATH = "/tmp/process.bpmn";
    static final String BPMN_ID = "fusebox_process";

    @BeforeAll
    static void resetAll() {
        try {
            smService.deleteSubmodelElement(BasyxOperationDeployer.DEPLOY_PROCESS_OP_IDSHORT);
        } catch (Exception e) {
        }
        try {
            Files.delete(Paths.get(BPMN_RESOURCE_PATH));
        } catch (Exception e) {
        }
    }

    @Test
    void completeScenario() throws MqttException, InterruptedException {
        sendMessageToTopic();

        waitForPropagation();

        assertProcessResourceWasCreated();

        assertOperationWasAdded();

        executeDeployOperationInAas();

        waitForPropagation();

        assertProcessWasDeployed();
    }

    private void assertProcessResourceWasCreated() {
        assertThat(Files.exists(Paths.get(BPMN_RESOURCE_PATH))).isTrue();
    }

    private void assertOperationWasAdded() {
        assertDoesNotThrow(() -> smService.getSubmodelElement(BasyxOperationDeployer.DEPLOY_PROCESS_OP_IDSHORT));
    }

    private void sendMessageToTopic() throws MqttException {
        try (MqttClient client = new MqttClient(MQTT_SERVERURI, "testClient")) {
            client.connect();
            client.publish(MQTT_TOPIC, new MqttMessage("SimpleTrigger".getBytes()));
            client.disconnect();
        }
    }

    private void executeDeployOperationInAas() {
        OperationVariable input = new DefaultOperationVariable.Builder()
                .value(new DefaultProperty.Builder()
                        .value("0").build())
                .build();

        smService.invokeOperation(BasyxOperationDeployer.DEPLOY_PROCESS_OP_IDSHORT, new OperationVariable[] { input });
    }

    private void assertProcessWasDeployed() {
        Optional<InspectedProcessInstance> firstProcessInstance = InspectionUtility
                .findProcessInstances()
                .withBpmnProcessId(BPMN_ID)
                .findFirstProcessInstance();

        BpmnAssert.assertThat(firstProcessInstance.get());
    }

    private void waitForPropagation() throws InterruptedException {
        Thread.sleep(3000);
    }

}
