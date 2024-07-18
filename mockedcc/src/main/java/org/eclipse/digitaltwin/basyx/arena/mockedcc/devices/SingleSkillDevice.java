package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import java.util.concurrent.CompletableFuture;

import org.eclipse.digitaltwin.basyx.arena.mockedcc.controllers.OperationChain.OperationFunctionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SingleSkillDevice implements OperationFunctionProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private DeviceStatus status = DeviceStatus.IDLE;

    private String message;

    abstract public String getDeviceName();

    public String getMessage() {
        return message;
    }

    protected void log(String message) {
        this.message = message;
        logger.info(message);
    }

    protected void error(String message) {
        setStatus(DeviceStatus.ERROR);
        this.message = message;
        logger.error(message);
    }

    public DeviceStatus getStatus() {
        return status;
    }

    protected void setStatus(DeviceStatus status) {
        this.status = status;
        logger.info("Status changed to " + status);
    }

    protected void doWork(Runnable runnable, String operationName) {
        setStatus(DeviceStatus.WORKING);
        log("Starting " + operationName);
        CompletableFuture.runAsync(runnable)
                .thenRun(() -> {
                    setStatus(DeviceStatus.IDLE);
                    log("Finished " + operationName);
                })
                .exceptionally(t -> {
                    error(t.getMessage());
                    return null;
                })
                .join();
    }

    protected void doSleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread was interrupted, but it's not allowed", e);
        }
    }
}
