package org.eclipse.digitaltwin.basyx.arena.processfactory.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;

public class BasyxProcessReader {

    private final ConnectedSubmodelService smService;

    public BasyxProcessReader(ConnectedSubmodelService smService) {
        this.smService = smService;
    }

    public InputStream read(String smElPath) throws FileNotFoundException {
        java.io.File file = smService.getFileByPath(smElPath);
        return new FileInputStream(file);
    }

}
