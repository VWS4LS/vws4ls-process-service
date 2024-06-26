package org.eclipse.digitaltwin.basyx.arena.processfactory.handlers;

import java.io.IOException;

import org.eclipse.digitaltwin.basyx.arena.processfactory.services.BasyxOperationDeployer;
import org.eclipse.digitaltwin.basyx.arena.processfactory.services.BasyxProcessReader;
import org.eclipse.digitaltwin.basyx.arena.processfactory.services.CamundaProcessManager;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * When a mqtt message is received, a BaSyx operation is deployed and a managed
 * process is added
 * 
 * @author mateusmolina
 */
@Component
public class KickstartMqttMessageHandler implements MessageHandler {
    private final CamundaProcessManager processManager;
    private final BasyxOperationDeployer operationDeployer;
    private final BasyxProcessReader processReader;

    public KickstartMqttMessageHandler(CamundaProcessManager processManager, BasyxProcessReader processReader,
            BasyxOperationDeployer operationDeployer) {
        this.processManager = processManager;
        this.processReader = processReader;
        this.operationDeployer = operationDeployer;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            processManager.addProcessResource(processReader.readProcess(), null);
            operationDeployer.deployOperations();
        } catch (IOException e) {
            throw new MessagingException(e.getMessage());
        }
    }
}
