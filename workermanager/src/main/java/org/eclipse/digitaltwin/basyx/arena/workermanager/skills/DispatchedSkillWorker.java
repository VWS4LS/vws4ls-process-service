package org.eclipse.digitaltwin.basyx.arena.workermanager.skills;

import java.time.LocalDateTime;

public record DispatchedSkillWorker<T>(Skill skill, T worker, LocalDateTime dispatchedTimestamp) {
}
