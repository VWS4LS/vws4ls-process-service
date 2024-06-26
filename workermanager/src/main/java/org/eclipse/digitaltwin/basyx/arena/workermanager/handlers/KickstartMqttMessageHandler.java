package org.eclipse.digitaltwin.basyx.arena.workermanager.handlers;

import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SkillReader;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SkillWorkerDispatcher;
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

    private final SkillWorkerDispatcher skillDispatcher;
    private final SkillReader skillReader;

    public KickstartMqttMessageHandler(SkillWorkerDispatcher skillDispatcher, SkillReader skillReader) {
        this.skillDispatcher = skillDispatcher;
        this.skillReader = skillReader;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        skillReader.readSkills().thenCompose(skillDispatcher::synchronizeSkills);
    }
}
