package org.eclipse.digitaltwin.basyx.arena.workermanager.services.helpers;

import java.util.Map;
import java.util.function.Function;

import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.basyx.arena.common.OperationChain;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;

public class OperationSkillBuilder {

    static public Skill buildSkill(String skillId, Operation operation, ConnectedSubmodelService submodelService) {
        return new Skill(skillId, buildSkillFunction(submodelService, operation));
    }

    static private Function<Map<String, Object>, Map<String, Object>> buildSkillFunction(
            ConnectedSubmodelService submodelService,
            Operation operation) {
        return inputMap -> OperationChain.from(inputMap)
                .mapOperation(vars -> submodelService.invokeOperation(operation.getIdShort(), vars))
                .endMap();
    }

}
