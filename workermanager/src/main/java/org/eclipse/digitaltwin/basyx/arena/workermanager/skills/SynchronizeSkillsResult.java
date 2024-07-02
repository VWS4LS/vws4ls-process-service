package org.eclipse.digitaltwin.basyx.arena.workermanager.skills;

public record SynchronizeSkillsResult<T>(Iterable<DispatchedSkillWorker<T>> dispatchedSkillWorkers,
        Iterable<DispatchedSkillWorker<T>> abortedSkillWorkers, Iterable<Skill> failedToDispatch) {

    public boolean failedToDispatchAny() {
        return failedToDispatch.iterator().hasNext();
    }
}
