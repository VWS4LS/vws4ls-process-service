package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.springframework.stereotype.Service;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobWorker;

/**
 * 
 * 
 * @author mateusmolina
 */
@Service
public class ZeebeWorkerManager {

    private final ZeebeClient zeebeClient;

    protected final Deque<JobWorker> workers = new ArrayDeque<>();

    public ZeebeWorkerManager(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    public JobWorker deployWorker(Skill skill) {
        JobWorker jobWorker = zeebeClient.newWorker().jobType(skill.skillId())
                .handler((jobClient, job) -> handleJob(jobClient, job, skill)).open();

        workers.add(jobWorker);

        return jobWorker;
    }

    public void abortWorker(JobWorker worker) {
        if (!workers.remove(worker))
            throw new RuntimeException("Worker " + worker + " isn't managed.");

        worker.close();
    }

    private static void handleJob(JobClient jobClient, ActivatedJob job, Skill skill) {
        Map<String, Object> outputs = skill.skillFunction().apply(job.getVariablesAsMap());

        jobClient.newCompleteCommand(job).variables(outputs).send().join();
    }

}
