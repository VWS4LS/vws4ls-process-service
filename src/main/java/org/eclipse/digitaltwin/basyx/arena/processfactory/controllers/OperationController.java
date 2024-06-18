package org.eclipse.digitaltwin.basyx.arena.processfactory.controllers;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.basyx.arena.processfactory.services.CamundaProcessManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OperationController {

    public static final String DEPLOY_PROCESS_MAPPING = "/deploy-process";

    private final CamundaProcessManager camundaProcessManager;

    public OperationController(CamundaProcessManager camundaProcessDeployer) {
        this.camundaProcessManager = camundaProcessDeployer;
    }

    @PostMapping(DEPLOY_PROCESS_MAPPING)
    public ResponseEntity<OperationVariable[]> deployProcessOperation(@RequestBody OperationVariable[] requestData) {
        camundaProcessManager.deployMostRecentProcess();
        return new ResponseEntity<>(requestData, HttpStatus.OK);
    }

}