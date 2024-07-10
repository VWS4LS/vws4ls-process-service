package org.eclipse.digitaltwin.basyx.arena.workermanager.services.helpers;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;

public class OperationSkillBuilder {

    static public Skill buildSkill(String skillId, Operation operation, ConnectedSubmodelService submodelService) {
        return new Skill(skillId, buildSkillFunction(submodelService, operation));
    }

    static private Function<Map<String, Object>, Map<String, Object>> buildSkillFunction(
            ConnectedSubmodelService submodelService,
            Operation operation) {
        return (inputMap) -> {
            OperationVariable[] outputs = submodelService.invokeOperation(operation.getIdShort(),
                    buildOperationVariablesFromMap(inputMap));

            return buildMapFromOperationVariables(outputs);
        };
    }

    static private OperationVariable[] buildOperationVariablesFromMap(
            Map<String, Object> inputs) {
        return inputs.entrySet().stream().map(entry -> buildOperationVariable(entry.getKey(), entry.getValue()))
                .toArray(OperationVariable[]::new);
    }

    static private OperationVariable buildOperationVariable(String idShort, Object value) {
        return new DefaultOperationVariable.Builder()
                .value(new DefaultProperty.Builder()
                        .idShort(idShort)
                        .valueType(DataTypeDefXsd.STRING)
                        .value(String.valueOf(value)).build())
                .build();
    }

    static private Map<String, Object> buildMapFromOperationVariables(OperationVariable[] outputs) {
        return List.of(outputs).stream().map(OperationVariable::getValue)
                .map(Property.class::cast)
                .collect(Collectors.toMap(Property::getIdShort, Property::getValue));
    }

}
