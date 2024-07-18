package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MonitoringDevice extends SingleSkillDevice {

    private boolean monitoringOut = false;

    final long delayToMonitor = 60 * 1000; // ms

    @Async
    @Override
    public Map<String, Object> apply(Map<String, Object> ins) {
        monitor();

        ins.clear();
        ins.put("monitoring_out", monitoringOut);

        return ins;
    }

    public void monitor() {
        doWork(() -> {
            doSleep(delayToMonitor);
            monitoringOut = true;
        }, "Monitor");
    }

    @Override
    public String getDeviceName() {
        return "MonitoringDevice";
    }

    public boolean isMonitoringOut() {
        return monitoringOut;
    }

}