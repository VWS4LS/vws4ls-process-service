package org.eclipse.digitaltwin.basyx.arena.mockedcc.controllers;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.basyx.arena.common.OperationChain;
import org.eclipse.digitaltwin.basyx.arena.mockedcc.devices.SingleSkillDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SingleSkillDevicesController {
    final static Logger logger = LoggerFactory.getLogger(SingleSkillDevicesController.class);

    @Autowired
    private List<SingleSkillDevice> devices;

    @GetMapping("/devices")
    public ResponseEntity<List<SingleSkillDevice>> getAllDevicesInformation() {
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/devices/{deviceName}")
    public ResponseEntity<SingleSkillDevice> getDeviceInformation(@PathVariable String deviceName) {
        return ResponseEntity.of(getDeviceFromDeviceName(deviceName));
    }

    @PostMapping("/devices/{deviceName}/invoke")
    public ResponseEntity<OperationVariable[]> invokeDeviceOperation(@RequestBody OperationVariable[] requestData,
            @PathVariable String deviceName) {
        return ResponseEntity.ok(OperationChain.from(requestData)
                .map(getDeviceFromDeviceName(deviceName).orElseThrow())
                .log(logger)
                .end());
    }

    private Optional<SingleSkillDevice> getDeviceFromDeviceName(String deviceName) {
        return devices.stream()
                .filter(d -> d.getDeviceName().equalsIgnoreCase(deviceName))
                .findFirst();
    }

}
