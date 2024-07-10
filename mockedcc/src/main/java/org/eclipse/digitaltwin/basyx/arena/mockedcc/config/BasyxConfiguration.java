package org.eclipse.digitaltwin.basyx.arena.mockedcc.config;

import java.util.Arrays;

import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class BasyxConfiguration {
    @Bean
    public ObjectMapper getObjectMapper() {
        return new BaSyxHTTPConfiguration()
                .jackson2ObjectMapperBuilder(Arrays.asList(new Aas4JHTTPSerializationExtension())).build();
    }
}
