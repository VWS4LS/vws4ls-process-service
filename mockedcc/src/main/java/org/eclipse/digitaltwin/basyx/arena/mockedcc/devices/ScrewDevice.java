package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import java.util.Map;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ScrewDevice extends SingleSkillDevice {
    private double desiredTorque = 0;
    private boolean torqueReached = false;

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
        doWork(() -> {
            if (desiredTorque == 0)
                throw new RuntimeException("Desired torque can't be equal to 0");
            
                this.desiredTorque = desiredTorque;

            double currentTorque = 0;


            while (desiredTorque >= currentTorque) {
                currentTorque += torqueStep;
                log("[" + currentTorque + "/" + desiredTorque + "]Nm Almost there...");
                doSleep(delayPerStep);
            }

            torqueReached = true;
        }, "Screw");
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

}