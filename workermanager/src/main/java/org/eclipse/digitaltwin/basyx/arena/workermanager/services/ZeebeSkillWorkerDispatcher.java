package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.DispatchedSkill;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SkillWorkerDispatcher;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SynchronizeSkillsResult;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.worker.JobWorker;

@Component
public class ZeebeSkillWorkerDispatcher implements SkillWorkerDispatcher {

    private final ZeebeWorkerManager workerManager;
    private final Deque<ZeebeDispatchedSkill> dispatchedSkillsWorkers = new ArrayDeque<>();

    public ZeebeSkillWorkerDispatcher(ZeebeWorkerManager workerDispatcher) {
        this.workerManager = workerDispatcher;
    }

    @Override
    public SynchronizeSkillsResult synchronizeSkills(Collection<Skill> skills) {
        Collection<ZeebeDispatchedSkill> abortedWorkers = abortExistingWorkers(skills);

        Collection<ZeebeDispatchedSkill> dispatchedWorkers = skills.stream().map(skill -> {
            JobWorker worker = workerManager.deployWorker(skill);
            if (worker.isOpen())
                return new ZeebeDispatchedSkill(skill, worker, LocalDateTime.now());
            else
                return null;
        }).toList();

        Collection<Skill> dispatchedSkills = dispatchedWorkers.stream().map(DispatchedSkill::getSkill).toList();
        Collection<Skill> failedToDispatch = new ArrayList<>(skills);
        failedToDispatch.removeAll(dispatchedSkills);

        dispatchedSkillsWorkers.removeAll(abortedWorkers);
        dispatchedSkillsWorkers.addAll(dispatchedWorkers);

        return new SynchronizeSkillsResult(List.copyOf(dispatchedWorkers), List.copyOf(abortedWorkers), failedToDispatch);
    }

    public Collection<DispatchedSkill> getDispatchedSkillWorkers() {
        return Collections.unmodifiableCollection(dispatchedSkillsWorkers);
    }

    private Collection<ZeebeDispatchedSkill> abortExistingWorkers(Collection<Skill> skills) {
        return dispatchedSkillsWorkers.stream()
                .filter(abortWorkerPredicate(skills))
                .map(ds -> {
                    workerManager.abortWorker(ds.getJobWorker());
                    return ds;
                })
                .toList();
    }

    private static Predicate<ZeebeDispatchedSkill> abortWorkerPredicate(Collection<Skill> skills) {
        return dispatchedSkillWorker -> skills.stream()
                .map(Skill::skillId)
                .collect(Collectors.toList())
                .contains(dispatchedSkillWorker.getSkill().skillId());
    }

}
