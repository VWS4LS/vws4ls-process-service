package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import java.util.Map;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ScanDevice extends SingleSkillDevice {

    private double scanPosition;
    private boolean scanFinished = false;

    final double scanTotal = 100;
    final double scanStep = 1;
    final long delayPerStep = 200; // ms

    public ScanDevice(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

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
        doWork(() -> {
            this.scanPosition = scanPosition;
            
            double scanProgress = 0;

            while (scanTotal >= scanProgress) {
                scanProgress += scanStep;
                log("[" + scanProgress / scanTotal * 100 + "%] Scanning...");
                doSleep(delayPerStep);
            }

            scanFinished = true;
        }, "Scan");

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

}