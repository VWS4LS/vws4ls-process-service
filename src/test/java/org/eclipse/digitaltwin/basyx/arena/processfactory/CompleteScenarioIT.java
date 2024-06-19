package org.eclipse.digitaltwin.basyx.arena.processfactory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.digitaltwin.basyx.arena.processfactory.services.BasyxOperationDeployer;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan(basePackages = "org.eclipse.digitaltwin.basyx.arena.processfactory")
class CompleteScenarioIT {

    static final String BASYX_OPERATIONSSMURL = "http://localhost:8081/submodels/aHR0cHM6Ly9leGFtcGxlLmNvbS9pZHMvc20vNjU5Ml85MDMyXzYwMzJfNjc2Nw%3D%3D";
    static final String MQTT_SERVERURI = "tcp://localhost:1884";
    static final String MQTT_TOPIC = "test_topic";
    static final String RESOURCE_PATH = "/tmp/process.bpmn";

    @BeforeAll
    static void resetAll() {
        try {
            new ConnectedSubmodelService(BASYX_OPERATIONSSMURL)
                    .deleteSubmodelElement(BasyxOperationDeployer.DEPLOY_PROCESS_OP_IDSHORT);
        } catch (Exception e) {
        }
        try {
            Files.delete(Paths.get(RESOURCE_PATH));
        } catch (Exception e) {
        }
    }

    @Test
    void completeScenario() throws MqttException, InterruptedException {
        sendMessageToTopic();

        waitForPropagation();

        assertProcessResourceWasCreated();

        assertOperationWasAdded();

        executeOperationInAas();

        assertProcessWasDeployed();
    }

    private void assertProcessResourceWasCreated() {
        assertThat(Files.exists(Paths.get(RESOURCE_PATH))).isTrue();
    }

    private void assertOperationWasAdded() {
        assertDoesNotThrow(() -> new ConnectedSubmodelService(BASYX_OPERATIONSSMURL)
                .getSubmodelElement(BasyxOperationDeployer.DEPLOY_PROCESS_OP_IDSHORT));
    }

    private void sendMessageToTopic() throws MqttException {
        try (MqttClient client = new MqttClient(MQTT_SERVERURI, "testClient")) {
            client.connect();
            client.publish(MQTT_TOPIC, new MqttMessage("SimpleTrigger".getBytes()));
            client.disconnect();
        }
    }

    private void executeOperationInAas() {
        // TODO Auto-generated method stub
    }

    private void assertProcessWasDeployed() {
        // TODO Auto-generated method stub
    }

    private void waitForPropagation() throws InterruptedException {
        Thread.sleep(3000);
    }

}
