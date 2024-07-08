package org.eclipse.digitaltwin.basyx.arena.processfactory.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.digitaltwin.basyx.arena.processfactory.config.BasyxSettings;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Reads a process from the configured process submodel
 *
 * @author mateusmolina
 */
@Service
public class BasyxProcessReader {

    private final ConnectedSubmodelService smService;
    private final String fileSEIdShort;

    private static final Logger logger = LoggerFactory.getLogger(BasyxProcessReader.class);

    public BasyxProcessReader(@Qualifier("processSubmodelService") ConnectedSubmodelService smService,
            BasyxSettings settings) {
        this.fileSEIdShort = settings.processFileSEIdShort();
        this.smService = smService;
    }

    public InputStream readProcess() throws FileNotFoundException {
        logger.info("Reading process from Submodel '" + smService.getSubmodel().getIdShort() + "' / FileSE '"
                + fileSEIdShort + "'");
        return read(smService, fileSEIdShort);
    }

    private static InputStream read(ConnectedSubmodelService service, String smElPath) throws FileNotFoundException {
        java.io.File file = service.getFileByPath(smElPath);
        return new FileInputStream(file);
    }

}
