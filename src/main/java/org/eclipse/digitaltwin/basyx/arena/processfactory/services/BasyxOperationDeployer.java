package org.eclipse.digitaltwin.basyx.arena.processfactory.services;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.QualifierKind;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultQualifier;
import org.eclipse.digitaltwin.basyx.arena.processfactory.config.ServerSettings;
import org.eclipse.digitaltwin.basyx.arena.processfactory.controllers.OperationController;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Deploys all process related operations
 * 
 * @author mateusmolina
 */
@Service
public class BasyxOperationDeployer {
    public static final String DEPLOY_PROCESS_OP_IDSHORT = "deployProcess";
    public static final String DEPLOY_PROCESS_OP_QUALIFIER_TYPE = "invocationDelegation";

    private final ConnectedSubmodelService smService;

    private final ServerSettings serverSettings;

    public BasyxOperationDeployer(@Qualifier("operationsSubmodelService") ConnectedSubmodelService smService,
            ServerSettings serverSettings) {
        this.smService = smService;
        this.serverSettings = serverSettings;
    }

    /**
     * Deploy all operations using server.externalUrl property as baseUrl
     */
    public void deployOperations() {
        deployOperations(serverSettings.externalUrl());
    }

    public void deployOperations(String baseUrl) {
        buildOperations(baseUrl).forEach(this::deployOperation);
    }

    private void deployOperation(Operation operation) {
        smService.createSubmodelElement(operation);
    }

    protected static List<Operation> buildOperations(String baseUrl) {
        return List.of(buildDeployProcessOperation(baseUrl));
    }

    protected static Operation buildDeployProcessOperation(String baseUrl) {
        return new DefaultOperation.Builder()
                .idShort(DEPLOY_PROCESS_OP_IDSHORT)
                .qualifiers(new DefaultQualifier.Builder()
                        .kind(QualifierKind.CONCEPT_QUALIFIER)
                        .type(DEPLOY_PROCESS_OP_QUALIFIER_TYPE)
                        .valueType(DataTypeDefXsd.STRING)
                        .value(baseUrl + OperationController.DEPLOY_PROCESS_MAPPING)
                        .build())
                .build();
    }
}