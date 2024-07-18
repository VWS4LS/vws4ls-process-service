package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ETestDevice extends SingleSkillDevice {

    final static Logger logger = LoggerFactory.getLogger(ScanDevice.class);

    private double etestPosition;
    private boolean etestPassed = false;
    private DeviceStatus status = DeviceStatus.IDLE;

    final double etestTotal = 100;
    final double etestStep = 1;
    final long delayPerStep = 200; // ms

    @Async
    @Override
    public Map<String, Object> apply(Map<String, Object> ins) {
        Optional.ofNullable(ins.get("etest_position"))
                .map(String::valueOf)
                .map(Double::parseDouble)
                .ifPresent(t -> {
                    this.etest(t);
                    ins.clear();
                    ins.put("etest_passed", etestPassed);
                });

        return ins;
    }

    public void etest(double etestPosition) {
        this.etestPosition = etestPosition;

        status = DeviceStatus.WORKING;

        logger.info("Starting E-Test operation");

        double etestProgress = 0;

        while (etestTotal >= etestProgress) {
            etestProgress += etestStep;
            logger.info("[" + etestProgress / etestTotal * 100 + "%] Testing...");
            try {
                Thread.sleep(delayPerStep);
            } catch (InterruptedException e) {
                logger.error("Error executing E-Test operation", e);
            }
        }

        status = DeviceStatus.IDLE;
        logger.info("Done testing");
        etestPassed = true;
    }

    @Override
    public String getDeviceName() {
        return "ETestDevice";
    }

    public double getEtestPosition() {
        return etestPosition;
    }

    public boolean isEtestPassed() {
        return etestPassed;
    }

    public DeviceStatus getStatus() {
        return status;
    }

}