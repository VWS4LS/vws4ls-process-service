package org.eclipse.digitaltwin.basyx.arena.workermanager.skills;

public record SynchronizeSkillsResult(Iterable<DispatchedSkill> dispatchedSkillWorkers,
        Iterable<DispatchedSkill> abortedSkills, Iterable<Skill> failedToDispatch) {

    public boolean failedToDispatchAny() {
        return failedToDispatch.iterator().hasNext();
    }
}
