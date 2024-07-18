package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

public enum DeviceStatus {
    WORKING("WORKING"), IDLE("IDLE"), ERROR("ERROR");

    String status;

    DeviceStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.status;
    }
}