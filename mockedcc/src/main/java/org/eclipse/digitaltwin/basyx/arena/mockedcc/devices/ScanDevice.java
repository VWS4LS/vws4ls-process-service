package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ScanDevice extends SingleSkillDevice {

    final static Logger logger = LoggerFactory.getLogger(ScanDevice.class);

    private double scanPosition;
    private boolean scanFinished = false;
    private DeviceStatus status = DeviceStatus.IDLE;

    final double scanTotal = 100;
    final double scanStep = 1;
    final long delayPerStep = 200; // ms

    @Async
    @Override
    public Map<String, Object> apply(Map<String, Object> ins) {
        Optional.ofNullable(ins.get("scan_position"))
                .map(String::valueOf)
                .map(Double::parseDouble)
                .ifPresent(t -> {
                    this.scan(t);
                    ins.clear();
                    ins.put("scan_finished", scanFinished);
                });

        return ins;
    }

    public void scan(double scanPosition) {
        this.scanPosition = scanPosition;

        status = DeviceStatus.WORKING;

        logger.info("Starting scan operation");

        double scanProgress = 0;

        while (scanTotal >= scanProgress) {
            scanProgress += scanStep;
            logger.info("[" + scanProgress / scanTotal * 100 + "%] Scanning...");
            try {
                Thread.sleep(delayPerStep);
            } catch (InterruptedException e) {
                logger.error("Error executing scan operation", e);
            }
        }

        status = DeviceStatus.IDLE;
        logger.info("Done scanning");
        scanFinished = true;
    }

    @Override
    public String getDeviceName() {
        return "ScanDevice";
    }

    public double getScanPosition() {
        return scanPosition;
    }

    public boolean isScanFinished() {
        return scanFinished;
    }

    public DeviceStatus getStatus() {
        return status;
    }

}