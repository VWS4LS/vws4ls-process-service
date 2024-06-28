package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.ConnectedAasManager;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.arena.workermanager.config.BasyxSettings;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SkillReader;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncoder;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.springframework.stereotype.Service;

@Service
public class BasyxSkillReader implements SkillReader {

    private final String qualifierTypeOfSkillProvider;
    private final String aasRepositoryUrl;
    private final ConnectedAasManager aasManager;
    private final ConnectedAasRepository aasRepository;

    public static final String OPERATION_API_PATH = "/submodels/%s/submodel-elements/%s/invoke";

    public BasyxSkillReader(BasyxSettings settings, ConnectedAasRepository aasRepository,
            ConnectedAasManager aasManager) {
        this.aasRepositoryUrl = settings.aasRepositoryUrl();
        this.qualifierTypeOfSkillProvider = settings.qualifierSkillProvider();
        this.aasManager = aasManager;
        this.aasRepository = aasRepository;
    }

    @Override
    public CompletableFuture<List<Skill>> readSkills() {
        return CompletableFuture
                .supplyAsync(() -> aasRepository.getAllAas(PaginationInfo.NO_LIMIT).getResult())
                .thenCompose(this::readSkillsFromShells);
    }

    private CompletableFuture<List<Skill>> readSkillsFromShells(List<AssetAdministrationShell> shells) {
        List<CompletableFuture<List<Skill>>> futures = shells.stream()
                .map(shell -> CompletableFuture.supplyAsync(() -> aasManager.getAllSubmodels(shell.getId()))
                        .thenCompose(this::readSkillsFromSubmodels))
                .toList();

        return getAllOf(futures);
    }

    private CompletableFuture<List<Skill>> readSkillsFromSubmodels(List<ConnectedSubmodelService> submodelServices) {
        List<CompletableFuture<List<Skill>>> futures = submodelServices.stream()
                .map(this::readSkillsFromSubmodel)
                .toList();

        return getAllOf(futures);
    }

    private CompletableFuture<List<Skill>> readSkillsFromSubmodel(ConnectedSubmodelService submodelService) {
        return CompletableFuture
                .supplyAsync(() -> submodelService.getSubmodelElements(new PaginationInfo(null, null)).getResult().stream()
                        .map(se -> getSkillFromSubmodelElementIfAny(submodelService.getSubmodel().getId(), se))
                        .flatMap(Optional::stream)
                        .toList());
    }

    private Optional<Skill> getSkillFromSubmodelElementIfAny(String submodelId, SubmodelElement se) {
        Optional<String> skillId = se.getQualifiers().stream()
                .filter(qualifier -> qualifier.getType().equals(qualifierTypeOfSkillProvider))
                .findFirst().map(Qualifier::getValue);

        return skillId.map(id -> new FullyQualifiedOperation(submodelId, se.getIdShort(), id))
                .map(this::buildSkill);
    }

    private Skill buildSkill(FullyQualifiedOperation operation) {
        return new Skill(operation.skillId(), buildSkillEndpoint(operation));
    }

    private String buildSkillEndpoint(FullyQualifiedOperation operation) {
        String submodelIdBase64 = Base64UrlEncoder.encode(operation.submodelId());
        return aasRepositoryUrl + String.format(OPERATION_API_PATH, submodelIdBase64, operation.operationSeIdShort());
    }

    static CompletableFuture<List<Skill>> getAllOf(List<CompletableFuture<List<Skill>>> futures) {
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));

        return allOf.thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList()));
    }

    record FullyQualifiedOperation(String submodelId, String operationSeIdShort, String skillId) {
    }
}
