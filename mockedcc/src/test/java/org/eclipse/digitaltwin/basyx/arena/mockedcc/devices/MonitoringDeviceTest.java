package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.BrokenBarrierException;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

public class MonitoringDeviceTest {
    ApplicationEventPublisher applicationEventPublisher = new ApplicationEventPublisher() {
        @Override
        public void publishEvent(Object event) {
            System.out.println(event);
        }
    };

    @Test
    void testMonitor() throws InterruptedException, BrokenBarrierException {
        MonitoringDevice monitoringDevice = new MonitoringDevice(applicationEventPublisher);

        Thread t = new Thread(() -> monitoringDevice.monitor());
        t.start();

        Thread.sleep(100);

        assertEquals(DeviceStatus.WORKING, monitoringDevice.getStatus());
        monitoringDevice.handleStatusChangedEvent(
                new StatusChangedEvent(new ScrewDevice(applicationEventPublisher), DeviceStatus.WORKING,
                        DeviceStatus.IDLE));

        monitoringDevice.handleStatusChangedEvent(
                new StatusChangedEvent(new ScanDevice(applicationEventPublisher), DeviceStatus.WORKING,
                        DeviceStatus.IDLE));
        monitoringDevice.handleStatusChangedEvent(
                new StatusChangedEvent(new ETestDevice(applicationEventPublisher), DeviceStatus.WORKING,
                        DeviceStatus.IDLE));

        t.join();

        assertEquals(DeviceStatus.IDLE, monitoringDevice.getStatus());
    }
}
