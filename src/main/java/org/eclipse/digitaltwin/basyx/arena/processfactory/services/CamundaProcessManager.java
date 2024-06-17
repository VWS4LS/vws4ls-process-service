package org.eclipse.digitaltwin.basyx.arena.processfactory.services;

import java.io.InputStream;

import org.springframework.stereotype.Service;

import io.camunda.zeebe.client.ZeebeClient;

@Service
public class CamundaProcessManager {

    public static final String BPMN_RES_PATH = "/process.bpmn";

    private final ZeebeClient zeebeClient;

    public CamundaProcessManager(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    public void deployProcessIfAny() {
        deployProcess(BPMN_RES_PATH);
    }

    public void deployProcess(String path) {
        zeebeClient.newDeployResourceCommand().addResourceFile(path).send();
    }

    public void addProcess(InputStream is, String fileName) {

    }
}
