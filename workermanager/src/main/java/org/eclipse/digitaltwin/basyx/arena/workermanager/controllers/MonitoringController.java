package org.eclipse.digitaltwin.basyx.arena.workermanager.controllers;

import java.util.Collection;

import org.eclipse.digitaltwin.basyx.arena.workermanager.services.ZeebeSkillWorkerDispatcher;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.DispatchedSkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitoringController {

    @Autowired
    private ZeebeSkillWorkerDispatcher dispatcher;

    @GetMapping("/deployed-skills")
    public ResponseEntity<Collection<DispatchedSkill>> getDeployedSkills() {
        return ResponseEntity.ok(dispatcher.getDispatchedSkillWorkers());
    }

}
