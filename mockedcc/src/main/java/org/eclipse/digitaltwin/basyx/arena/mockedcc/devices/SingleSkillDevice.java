package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import java.util.ArrayDeque;
import java.util.Deque;

import org.eclipse.digitaltwin.basyx.arena.common.OperationFunctionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class SingleSkillDevice implements OperationFunctionProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private DeviceStatus status = DeviceStatus.IDLE;

    private Deque<String> messages = new ArrayDeque<>();

    private ApplicationEventPublisher applicationEventPublisher;

    public SingleSkillDevice(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    abstract public String getDeviceName();

    public String getCurrentMessage() {
        return messages.peekLast();
    }

    @JsonIgnore
    public Deque<String> getMessages() {
        return new ArrayDeque<>(messages);
    }

    protected synchronized void log(String message) {
        this.messages.add(message);
        logger.info(message);
    }

    protected synchronized void error(String message) {
        setStatus(DeviceStatus.ERROR);
        this.messages.add(message);
        logger.error(message);
    }

    public DeviceStatus getStatus() {
        return status;
    }

    protected void setStatus(DeviceStatus status) {
        applicationEventPublisher.publishEvent(new StatusChangedEvent(this, this.status, status));
        this.status = status;
        logger.info("Status changed to " + status);
    }

    protected void doWork(Runnable runnable, String operationName) {
        setStatus(DeviceStatus.WORKING);
        log("Starting " + operationName);
        try {
            runnable.run();
            setStatus(DeviceStatus.IDLE);
            log("Finished " + operationName);
        } catch (Exception e) {
            error(e.getMessage());
            throw e;
        }
    }

    protected void doSleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread was interrupted, but it's not allowed", e);
        }
    }
}
