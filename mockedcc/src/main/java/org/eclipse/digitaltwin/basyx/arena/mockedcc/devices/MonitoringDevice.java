package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MonitoringDevice extends SingleSkillDevice {

    final static Logger logger = LoggerFactory.getLogger(MonitoringDevice.class);

    private boolean monitoringOut = false;
    private DeviceStatus status = DeviceStatus.IDLE;

    final long delayToMonitor = 10000; // ms

    @Async
    @Override
    public Map<String, Object> apply(Map<String, Object> ins) {
        monitor();

        ins.clear();
        ins.put("monitoring_out", monitoringOut);

        return ins;
    }

    public void monitor() {

        status = DeviceStatus.WORKING;
        logger.info("Starting monitoring operation");

        try {
            Thread.sleep(delayToMonitor);
        } catch (InterruptedException e) {
            logger.error("Error executing screw operation", e);
        }

        logger.info("Done monitoring");

        status = DeviceStatus.IDLE;
        monitoringOut = true;

    }

    @Override
    public String getDeviceName() {
        return "MonitoringDevice";
    }

    public boolean isMonitoringOut() {
        return monitoringOut;
    }

    public DeviceStatus getStatus() {
        return status;
    }

}