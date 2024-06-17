package org.eclipse.digitaltwin.basyx.arena.processfactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CompleteScenarioIT {

    @BeforeAll
    public static void setup() {
        initBaSyx();
        initMqtt();
        initCamunda();
    }

    @Test
    public void completeScenario() {
        sendMessageToTopic();

        executeOperationInAas();

        checkCamundaDeployment();
    }

    private void checkCamundaDeployment() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkCamundaDeployment'");
    }

    private void sendMessageToTopic() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMessageToTopic'");
    }

    private void executeOperationInAas() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeOperation'");
    }

    private static void initCamunda() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initCamunda'");
    }

    private static void initMqtt() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initMqtt'");
    }

    private static void initBaSyx() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initBaSyx'");
    }

}
