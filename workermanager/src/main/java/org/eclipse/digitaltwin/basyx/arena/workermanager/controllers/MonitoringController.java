package org.eclipse.digitaltwin.basyx.arena.workermanager.controllers;

import java.util.List;

import org.eclipse.digitaltwin.basyx.arena.workermanager.services.ZeebeSkillWorkerDispatcher;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.DispatchedSkillWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitoringController {

    @Autowired
    private ZeebeSkillWorkerDispatcher dispatcher;

    @GetMapping("/deployed-skills")
    public ResponseEntity<List<DeploymentReport>> getDeployedSkills() {
        return ResponseEntity.ok(dispatcher.getDispatchedSkillWorkers().stream()
                .map(ds -> new DeploymentReport(ds, ds.worker().isOpen()))
                .toList());
    }

    private record DeploymentReport(DispatchedSkillWorker<?> skillWorker, boolean healthy) {
    }
}
