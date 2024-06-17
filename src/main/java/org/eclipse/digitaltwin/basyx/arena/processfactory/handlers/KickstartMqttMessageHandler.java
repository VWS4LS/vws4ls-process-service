package org.eclipse.digitaltwin.basyx.arena.processfactory.handlers;

import org.eclipse.digitaltwin.basyx.arena.processfactory.services.BasyxOperationDeployer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * When a mqtt message is received, a BaSyx operation is deployed
 */
@Component
public class KickstartMqttMessageHandler implements MessageHandler {

    private final BasyxOperationDeployer operationDeployer;

    public KickstartMqttMessageHandler(BasyxOperationDeployer operationDeployer) {
        this.operationDeployer = operationDeployer;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        operationDeployer.deployOperations();
    }

}
