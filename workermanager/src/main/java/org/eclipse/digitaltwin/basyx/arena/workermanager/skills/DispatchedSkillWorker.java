package org.eclipse.digitaltwin.basyx.arena.workermanager.skills;

import java.util.Date;

public record DispatchedSkillWorker(Skill skill, long workerId, Date dispatchedTimestamp) {
}
