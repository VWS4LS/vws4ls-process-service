package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ZeebeWorkerManager.class);

    private final ZeebeClient zeebeClient;
    private final ExecutorService executorService;

    protected final Deque<JobWorker> workers = new ArrayDeque<>();

    public ZeebeWorkerManager(ZeebeClient zeebeClient, ExecutorService executorService) {
        this.zeebeClient = zeebeClient;
        this.executorService = executorService;
    }

    public JobWorker deployWorker(Skill skill) {
        JobWorker jobWorker = zeebeClient.newWorker().jobType(skill.skillId())
                .handler((jobClient, job) -> executorService.submit(() -> handleJob(jobClient, job, skill)))
                .open();

        workers.add(jobWorker);

        return jobWorker;
    }

    public void abortWorker(JobWorker worker) {
        if (!workers.remove(worker))
            throw new RuntimeException("Worker " + worker + " isn't managed.");

        worker.close();
    }

    private void handleJob(JobClient jobClient, ActivatedJob job, Skill skill) {
        logger.info("Handling job: {} by thread: {}", job.getKey(), Thread.currentThread().getName());
        Map<String, Object> outputs = skill.skillFunction().apply(job.getVariablesAsMap());

        jobClient.newCompleteCommand(job).variables(outputs).send().join();
        logger.info("Completed job: {} by thread: {}", job.getKey(), Thread.currentThread().getName());
    }
}
