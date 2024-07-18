package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ScrewDevice extends SingleSkillDevice {

    final static Logger logger = LoggerFactory.getLogger(ScrewDevice.class);

    private double desiredTorque = 0;
    private boolean torqueReached = false;
    private DeviceStatus status = DeviceStatus.IDLE;

    final double torqueStep = 0.5;
    final long delayPerStep = 200; // ms

    @Async
    @Override
    public Map<String, Object> apply(Map<String, Object> ins) {
        Optional.ofNullable(ins.get("desired_torque"))
                .map(String::valueOf)
                .map(Double::parseDouble)
                .ifPresent(t -> {
                    this.screw(t);
                    ins.clear();
                    ins.put("torque_reached", torqueReached);
                });

        return ins;
    }

    public void screw(double desiredTorque) {
        if (desiredTorque == 0) {
            status = DeviceStatus.ERROR;
            logger.warn("Can't operate with DesiredTorque set to 0");
            return;
        }

        this.desiredTorque = desiredTorque;

        double currentTorque = 0;

        status = DeviceStatus.WORKING;
        logger.info("Starting screw operation");

        while (desiredTorque >= currentTorque) {
            currentTorque += torqueStep;
            logger.info("[" + currentTorque + "/" + desiredTorque + "]Nm Almost there...");
            try {
                Thread.sleep(delayPerStep);
            } catch (InterruptedException e) {
                logger.error("Error executing screw operation", e);
            }
        }

        logger.info("Done");

        status = DeviceStatus.IDLE;
        torqueReached = true;
    }

    @Override
    public String getDeviceName() {
        return "ScrewDevice";
    }

    public double getDesiredTorque() {
        return desiredTorque;
    }

    public boolean isTorqueReached() {
        return torqueReached;
    }

    public DeviceStatus getStatus() {
        return status;
    }

}