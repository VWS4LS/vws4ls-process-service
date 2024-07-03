package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.DispatchedSkillWorker;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SkillWorkerDispatcher;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SynchronizeSkillsResult;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.worker.JobWorker;

@Component
public class ZeebeSkillWorkerDispatcher implements SkillWorkerDispatcher {

    private final ZeebeWorkerManager workerManager;
    private final Deque<DispatchedSkillWorker<JobWorker>> dispatchedSkillsWorkers = new ArrayDeque<>();

    public ZeebeSkillWorkerDispatcher(ZeebeWorkerManager workerDispatcher) {
        this.workerManager = workerDispatcher;
    }

    @Override
    public SynchronizeSkillsResult<JobWorker> synchronizeSkills(Collection<Skill> skills) {
        Collection<DispatchedSkillWorker<JobWorker>> abortedWorkers = abortExistingWorkers(skills);

        Collection<DispatchedSkillWorker<JobWorker>> dispatchedWorkers = skills.stream().map(skill -> {
            JobWorker worker = workerManager.deployWorker(skill);
            if (worker.isOpen())
                return new DispatchedSkillWorker<>(skill, worker, LocalDateTime.now());
            else
                return null;
        }).toList();

        Collection<Skill> dispatchedSkills = dispatchedWorkers.stream().map(DispatchedSkillWorker::skill).toList();
        Collection<Skill> failedToDispatch = new ArrayList<>(skills);
        failedToDispatch.removeAll(dispatchedSkills);

        dispatchedSkillsWorkers.removeAll(abortedWorkers);
        dispatchedSkillsWorkers.addAll(dispatchedWorkers);

        return new SynchronizeSkillsResult<>(dispatchedWorkers, abortedWorkers, failedToDispatch);
    }

    public Collection<DispatchedSkillWorker<JobWorker>> getDispatchedSkillWorkers() {
        return Collections.unmodifiableCollection(dispatchedSkillsWorkers);
    }

    private Collection<DispatchedSkillWorker<JobWorker>> abortExistingWorkers(Collection<Skill> skills) {
        return dispatchedSkillsWorkers.stream()
                .filter(abortWorkerPredicate(skills))
                .map(ds -> {
                    workerManager.abortWorker(ds.worker());
                    return ds;
                })
                .toList();
    }

    private static Predicate<DispatchedSkillWorker<JobWorker>> abortWorkerPredicate(Collection<Skill> skills) {
        return dispatchedSkillWorker -> skills.stream()
                .map(Skill::skillId)
                .collect(Collectors.toList())
                .contains(dispatchedSkillWorker.skill().skillId());
    }
}
