package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import java.time.LocalDateTime;

import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.DispatchedSkill;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;

import io.camunda.zeebe.client.api.worker.JobWorker;

public class ZeebeDispatchedSkill extends DispatchedSkill {

    private final JobWorker jobWorker;

    public ZeebeDispatchedSkill(Skill skill, JobWorker jobWorker, LocalDateTime timestamp) {
        super(skill, timestamp);
        this.jobWorker = jobWorker;
    }

    public JobWorker getJobWorker() {
        return jobWorker;
    }

    @Override
    public boolean isHealthy() {
        return jobWorker.isOpen();
    }

}
