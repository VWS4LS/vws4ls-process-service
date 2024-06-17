package org.eclipse.digitaltwin.basyx.arena.processfactory.config;

import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BasyxConfiguration {

    @Bean
    public ConnectedSubmodelService getSubmodelService() {
        return new ConnectedSubmodelService("");
    }
}
