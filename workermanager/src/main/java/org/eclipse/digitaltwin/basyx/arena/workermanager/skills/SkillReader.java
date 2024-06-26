package org.eclipse.digitaltwin.basyx.arena.workermanager.skills;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Reads skills from a skill source.
 * 
 * A skill is an implementation of a capability.
 * 
 * @author mateusmolina
 */
public interface SkillReader {
    /**
     * Performs a read operation on the skill source
     * 
     */
    public CompletableFuture<List<Skill>> readSkills();
}
