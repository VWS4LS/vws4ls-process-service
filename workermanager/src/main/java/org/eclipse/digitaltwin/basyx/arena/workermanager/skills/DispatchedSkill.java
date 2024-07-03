package org.eclipse.digitaltwin.basyx.arena.workermanager.skills;

import java.time.LocalDateTime;

public abstract class DispatchedSkill {
    private final Skill skill;
    private final LocalDateTime timestamp;

    public DispatchedSkill(Skill skill, LocalDateTime timestamp) {
        this.skill = skill;
        this.timestamp = timestamp;
    }

    public Skill getSkill() {
        return skill;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    abstract public boolean isHealthy();
}
