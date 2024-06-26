package org.eclipse.digitaltwin.basyx.arena.workermanager.handlers;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * When a mqtt message is received
 * 
 * @author mateusmolina
 */
@Component
public class KickstartMqttMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        // todo: not implemented
    }
}
