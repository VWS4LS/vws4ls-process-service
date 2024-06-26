package org.eclipse.digitaltwin.basyx.arena.processfactory.config;

import java.util.Arrays;

import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    @Bean
    public ObjectMapper getObjectMapper() {
        return new BaSyxHTTPConfiguration()
                .jackson2ObjectMapperBuilder(Arrays.asList(new Aas4JHTTPSerializationExtension())).build();
    }
}
