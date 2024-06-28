package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.ConnectedAasManager;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.arena.workermanager.config.BasyxSettings;
import org.eclipse.digitaltwin.basyx.arena.workermanager.services.helpers.OperationSkillBuilder;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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

    static final List<String> testOperations = List
                    .of("monitoring-service", "scan-service", "screw-service", "electrical_test-service");

    static final ConnectedAasManager aasManager = new ConnectedAasManager(settings.aasRegistryUrl(),
            settings.aasRepositoryUrl(),
            settings.submodelRegistryUrl(), settings.submodelRepositoryUrl());

    static final ConnectedAasRepository aasRepository = new ConnectedAasRepository(settings.aasRepositoryUrl());

    @Test
    void testReadSkills() {
        BasyxSkillReader skillReader = new BasyxSkillReader(settings, aasRepository, aasManager);

        List<Skill> skills = skillReader.readSkills().join();

        List<String> skillIds = skills.stream().map(Skill::skillId).toList();

        List<Skill> expectedSkills = buildExpectedSkills();

        List<String> expectedSkillIds = expectedSkills.stream().map(Skill::skillId).toList();

        assertThat(skillIds).containsExactlyInAnyOrderElementsOf(expectedSkillIds);
}

@Test
void testBuildSkill() {
        String operationIdShort = "TestOperation";
        String skillId = "test-skill";
        ConnectedSubmodelService mockedService = Mockito.mock(ConnectedSubmodelService.class);

        List<OperationVariable> inVars = List.of(buildOperationVariable("in10", "default10"),
                        buildOperationVariable("in11", "default11"));
        List<OperationVariable> outVars = List.of(buildOperationVariable("out20", "default20"),
                        buildOperationVariable("out21", "default21"));

        OperationVariable[] outArray = outVars.toArray(OperationVariable[]::new);

        Map<String, Object> inMap = inVars.stream().map(OperationVariable::getValue).map(Property.class::cast)
                        .collect(Collectors.toMap(Property::getIdShort, Property::getValue));
        Map<String, Object> outMap = outVars.stream().map(OperationVariable::getValue).map(Property.class::cast)
                        .collect(Collectors.toMap(Property::getIdShort, Property::getValue));

        Operation testOperation = new DefaultOperation.Builder()
                        .idShort(operationIdShort)
                        .inputVariables(inVars)
                        .outputVariables(outVars)
                        .build();

        Skill actualSkill = OperationSkillBuilder.buildSkill(skillId, testOperation, mockedService);

        when(mockedService.invokeOperation(eq(operationIdShort), any())).thenReturn(outArray);

        Map<String, Object> actualOutMap = actualSkill.skillFunction().apply(inMap);

        assertThat(actualOutMap).isEqualTo(outMap);
    }

    static OperationVariable buildOperationVariable(String idShort, String value) {
            return new DefaultOperationVariable.Builder()
                            .value(new DefaultProperty.Builder().idShort(idShort).value(value).build()).build();
    }

    static List<Skill> buildExpectedSkills() {
            return testOperations.stream().map(BasyxSkillReaderTest::buildTestSkill).toList();
    }

    static Skill buildTestSkill(String skillId) {
            return new Skill(skillId, null);
    }

}
