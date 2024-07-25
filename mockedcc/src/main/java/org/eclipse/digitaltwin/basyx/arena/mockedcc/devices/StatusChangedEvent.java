package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import org.springframework.context.ApplicationEvent;

public class StatusChangedEvent extends ApplicationEvent {
    private DeviceStatus newStatus;
    private DeviceStatus previousStatus;

    public StatusChangedEvent(SingleSkillDevice source, DeviceStatus previousStatus, DeviceStatus newStatus) {
        super(source);
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }

    public DeviceStatus getNewStatus() {
        return newStatus;
    }

    public DeviceStatus getPreviousStatus() {
        return previousStatus;
    }
}
