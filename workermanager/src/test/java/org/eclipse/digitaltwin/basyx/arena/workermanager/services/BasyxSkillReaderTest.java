package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasenvironment.client.ConnectedAasManager;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.arena.workermanager.config.BasyxSettings;
import org.eclipse.digitaltwin.basyx.arena.workermanager.services.BasyxSkillReader.FullyQualifiedOperation;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncoder;
import org.junit.jupiter.api.Test;

/**
 * Requires running CI compose
 */
class BasyxSkillReaderTest {

    static final String AAS_REPO_URL = "http://localhost:8081";
    static final String SM_REPO_URL = "http://localhost:8081";
    static final String AAS_REG_URL = "http://localhost:9081";
    static final String SM_REG_URL = "http://localhost:9082";
    static final String QUALIFIER_TYPE = "skill-provider";

    static final BasyxSettings settings = new BasyxSettings(AAS_REG_URL, AAS_REPO_URL, SM_REG_URL, SM_REPO_URL,
            QUALIFIER_TYPE);

    static final List<FullyQualifiedOperation> testOperations = List
            .of(new FullyQualifiedOperation("https://example.com/ids/sm/6592_9032_6032_6767", "MonitoringOperation",
                    "monitoring-service"),
                    new FullyQualifiedOperation("https://example.com/ids/sm/6343_9032_6032_4658", "ScanOperation",
                            "scan-service"),
                    new FullyQualifiedOperation("https://example.com/ids/sm/9343_9032_6032_7430", "ScrewOperation",
                            "screw-service"),
                    new FullyQualifiedOperation("https://example.com/ids/sm/0443_9032_6032_5215",
                            "ElectricalTestOperation",
                            "electrical_test-service"));

    static final ConnectedAasManager aasManager = new ConnectedAasManager(settings.aasRegistryUrl(),
            settings.aasRepositoryUrl(),
            settings.submodelRegistryUrl(), settings.submodelRepositoryUrl());

    static final ConnectedAasRepository aasRepository = new ConnectedAasRepository(settings.aasRepositoryUrl());

    @Test
    void testReadSkills() {
        BasyxSkillReader skillReader = new BasyxSkillReader(settings, aasRepository, aasManager);

        List<Skill> skills = skillReader.readSkills().join();

        List<Skill> expectedSkills = buildExpectedSkills();

        assertThat(skills).containsExactlyInAnyOrderElementsOf(expectedSkills);
    }

    static List<Skill> buildExpectedSkills() {
        return testOperations.stream().map(BasyxSkillReaderTest::buildSkill).toList();
    }

    static Skill buildSkill(FullyQualifiedOperation operation) {
        return new Skill(operation.skillId(), buildSkillEndpoint(operation));
    }

    static String buildSkillEndpoint(FullyQualifiedOperation operation) {
        String submodelIdBase64 = Base64UrlEncoder.encode(operation.submodelId());
        String api = "/submodels/%s/submodel-elements/%s/invoke";
        return AAS_REPO_URL + String.format(api, submodelIdBase64, operation.operationSeIdShort());
    }

}
