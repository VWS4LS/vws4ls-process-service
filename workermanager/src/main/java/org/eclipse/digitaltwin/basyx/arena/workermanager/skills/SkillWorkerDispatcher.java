package org.eclipse.digitaltwin.basyx.arena.workermanager.skills;

import java.util.Collection;

/**
 * Dispatches workers, capable of perfoming a skills
 * 
 * @author mateusmolina
 */
public interface SkillWorkerDispatcher<T> {

    /**
     * Synchronize the collection of skills with the current dispatched skill
     * workers. This operation performs the following:
     * 
     * - dispatch workers for new skills;
     * - abort already dispatched workers, which are not within the collection;
     * - redispatch workers, from which the skills changed.
     * 
     * @param skills
     * @return SynchronizeSkillsResult
     */
    SynchronizeSkillsResult<T> synchronizeSkills(Collection<Skill> skills);

}