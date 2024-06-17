package org.eclipse.digitaltwin.basyx.arena.processfactory.services;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.QualifierKind;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultQualifier;
import org.eclipse.digitaltwin.basyx.arena.processfactory.controllers.OperationController;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Deploys all process related operations
 */
public class BasyxOperationDeployer {
    private final ConnectedSubmodelService smService;
    private final Operation[] operations = buildOperations();
    private static final String BASE_URL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

    public BasyxOperationDeployer(ConnectedSubmodelService smService) {
        this.smService = smService;
    }

    public void deployOperations() {
        List.of(operations).forEach(this::deployOperation);
    }

    private void deployOperation(Operation operation) {
        smService.createSubmodelElement(operation);
    }

    protected static Operation[] buildOperations() {
        Operation[] operations = { buildDeployProcessOperation() };
        return operations;
    }

    private static Operation buildDeployProcessOperation() {
        return new DefaultOperation.Builder()
                .idShort("deployProcess")
                .qualifiers(new DefaultQualifier.Builder()
                        .kind(QualifierKind.CONCEPT_QUALIFIER)
                        .type("invocationDelegation")
                        .valueType(DataTypeDefXsd.STRING)
                        .value(BASE_URL + OperationController.DEPLOY_PROCESS_MAPPING)
                        .build())
                .build();
    }
}