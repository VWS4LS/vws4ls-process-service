package org.eclipse.digitaltwin.basyx.arena.workermanager.handlers;

import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SkillReader;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SkillWorkerDispatcher;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SynchronizeSkillsResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * When a mqtt message is received, the set of skills is re-read and
 * resynchronized.
 * 
 * @author mateusmolina
 */
@Component
public class KickstartMqttMessageHandler implements MessageHandler {

    private final SkillWorkerDispatcher<?> skillDispatcher;
    private final SkillReader skillReader;

    public KickstartMqttMessageHandler(SkillWorkerDispatcher<?> skillDispatcher, SkillReader skillReader) {
        this.skillDispatcher = skillDispatcher;
        this.skillReader = skillReader;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        SynchronizeSkillsResult<?> result = skillReader.readSkills().thenApply(skillDispatcher::synchronizeSkills)
                .join();

        if (result.failedToDispatchAny())
            throw new MessagingException("Failed to dispatch following skills: " + result.failedToDispatch());
    }
}
