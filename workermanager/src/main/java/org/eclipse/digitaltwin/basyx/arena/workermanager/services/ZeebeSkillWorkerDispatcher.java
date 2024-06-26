package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;

import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.DispatchedSkillWorker;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SkillWorkerDispatcher;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SynchronizeSkillsResult;
import org.springframework.stereotype.Component;

@Component
public class ZeebeSkillWorkerDispatcher implements SkillWorkerDispatcher {

    private final ZeebeWorkerManager workerManager;
    private final Deque<DispatchedSkillWorker> dispatchedSkills = new ArrayDeque<>();

    public ZeebeSkillWorkerDispatcher(ZeebeWorkerManager workerDispatcher) {
        this.workerManager = workerDispatcher;
    }

    @Override
    public CompletableFuture<DispatchedSkillWorker> dispatchSkillWorker(Skill skill) {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    @Override
    public CompletableFuture<SynchronizeSkillsResult> synchronizeSkills(Collection<Skill> skills) {
        throw new UnsupportedOperationException("Unimplemented method");
    }
}
