package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.arena.workermanager.config.BasyxSettings;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SkillReader;
import org.springframework.stereotype.Service;

@Service
public class BasyxSkillReader implements SkillReader {

    private final String qualifierTypeOfSkillProvider;
    private final ConnectedAasRepository aasRepository;

    public BasyxSkillReader(BasyxSettings settings, ConnectedAasRepository aasRepository) {
        this.qualifierTypeOfSkillProvider = settings.qualifierSkillProvider();
        this.aasRepository = aasRepository;
    }

    @Override
    public CompletableFuture<List<Skill>> readSkills() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readSkills'");

    }

}
