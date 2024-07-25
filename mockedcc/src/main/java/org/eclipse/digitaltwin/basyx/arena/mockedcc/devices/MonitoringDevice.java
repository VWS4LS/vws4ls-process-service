package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MonitoringDevice extends SingleSkillDevice {

    private boolean monitoringOut = false;

    final long delayToMonitor = 60 * 1000; // ms

    private CountDownLatch latch;

    public MonitoringDevice(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Async
    @Override
    public Map<String, Object> apply(Map<String, Object> ins) {
        monitor();

        ins.clear();
        ins.put("monitoring_out", monitoringOut);

        return ins;
    }

    @EventListener
    public void handleStatusChangedEvent(StatusChangedEvent event) throws InterruptedException, BrokenBarrierException {
        if (event.getNewStatus() != DeviceStatus.IDLE)
            return;

        if (event.getSource() instanceof ScanDevice) {
            latch.countDown();
            log("ScanDevice finished");
        }
        if (event.getSource() instanceof ScrewDevice) {
            latch.countDown();
            log("ScrewDevice finished");
        }
        if (event.getSource() instanceof ETestDevice) {
            latch.countDown();
            log("ETestDevice finished");
        }
    }

    public void monitor() {
        doWork(() -> {
            try {
                latch = new CountDownLatch(3);
                latch.await();
                monitoringOut = true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "Monitoring");
    }

    @Override
    public String getDeviceName() {
        return "MonitoringDevice";
    }

    public boolean isMonitoringOut() {
        return monitoringOut;
    }

}