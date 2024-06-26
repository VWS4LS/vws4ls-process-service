package org.eclipse.digitaltwin.basyx.arena.workermanager.config;

import java.util.Arrays;

import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties({
        BasyxSettings.class
})
public class BasyxConfiguration {

    public ConnectedAasRepository getAasRepository(BasyxSettings settings) {
        return new ConnectedAasRepository(settings.aasRepositoryUrl());
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new BaSyxHTTPConfiguration()
                .jackson2ObjectMapperBuilder(Arrays.asList(new Aas4JHTTPSerializationExtension())).build();
    }
}
