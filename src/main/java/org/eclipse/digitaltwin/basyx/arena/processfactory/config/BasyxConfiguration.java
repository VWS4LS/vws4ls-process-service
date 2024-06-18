package org.eclipse.digitaltwin.basyx.arena.processfactory.config;

import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        BasyxSettings.class
})
public class BasyxConfiguration {

    @Bean(name = "processSubmodelService")
    public ConnectedSubmodelService getProcessSubmodelService(BasyxSettings settings) {
        return new ConnectedSubmodelService(settings.processSmUrl());
    }

    @Bean(name = "operationsSubmodelService")
    public ConnectedSubmodelService getOperationsSubmodelService(BasyxSettings settings) {
        return new ConnectedSubmodelService(settings.operationsSmUrl());
    }
}
