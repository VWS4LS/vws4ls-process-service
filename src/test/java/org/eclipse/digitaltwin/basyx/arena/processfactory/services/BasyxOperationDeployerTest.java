package org.eclipse.digitaltwin.basyx.arena.processfactory.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.basyx.arena.processfactory.config.ServerSettings;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Requires running basyx env instance
 */
class BasyxOperationDeployerTest {

    final static String BASYX_OP_SM_URL = "http://localhost:8081/submodels/aHR0cHM6Ly9leGFtcGxlLmNvbS9pZHMvc20vNjU5Ml85MDMyXzYwMzJfNjc2Nw%3D%3D";

    final static String SERVER_BASE_URL = "http://test-server";

    final static ConnectedSubmodelService service = new ConnectedSubmodelService(BASYX_OP_SM_URL);

    @BeforeEach
    void clearBasyxOperations() {
        service.deleteSubmodelElement(BasyxOperationDeployer.DEPLOY_PROCESS_OP_IDSHORT);
    }

    @Test
    void testDeployOperations() {
        BasyxOperationDeployer opDeployer = new BasyxOperationDeployer(service, new ServerSettings(SERVER_BASE_URL));

        opDeployer.deployOperations();

        Operation expectedOp = BasyxOperationDeployer.buildDeployProcessOperation(SERVER_BASE_URL);

        Operation actualOp = (Operation) service.getSubmodelElement(BasyxOperationDeployer.DEPLOY_PROCESS_OP_IDSHORT);

        assertThat(actualOp).isEqualTo(expectedOp);
    }
}
