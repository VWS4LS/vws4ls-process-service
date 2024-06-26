package org.eclipse.digitaltwin.basyx.arena.workermanager.skills;

public record SynchronizeSkillsResult(Iterable<DispatchedSkillWorker> dispatchedSkillWorkers,
        Iterable<DispatchedSkillWorker> abortedSkillWorkers, Iterable<Skill> failedToDispatch) {

    public boolean failedToDispatchAny() {
        return failedToDispatch.iterator().hasNext();
    }
}
