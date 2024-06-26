package org.eclipse.digitaltwin.basyx.arena.workermanager.skills;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Dispatches workers, capable of perfoming a skills
 * 
 * @author mateusmolina
 */
public interface SkillWorkerDispatcher {

    /**
     * Dispatches a worker capable of performing the passed skill
     * 
     * @param skill
     */
    CompletableFuture<DispatchedSkillWorker> dispatchSkillWorker(Skill skill);

    /**
     * Synchronize the collection of skills with the current dispatched skill
     * workers. This operation performs the following:
     * 
     * - dispatch workers for new skills;
     * - abort already dispatched workers, which are not within the collection;
     * - redispatch workers, from which the skills changed.
     * 
     * @param skills
     */
    CompletableFuture<SynchronizeSkillsResult> synchronizeSkills(Collection<Skill> skills);

}