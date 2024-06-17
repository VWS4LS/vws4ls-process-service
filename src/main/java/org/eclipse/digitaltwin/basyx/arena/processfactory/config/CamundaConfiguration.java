package org.eclipse.digitaltwin.basyx.arena.processfactory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;

@Configuration
public class CamundaConfiguration {
    @Bean
    public ZeebeClient getZeebeClient() {
        ZeebeClientBuilder builder = new ZeebeClientBuilderImpl();
        return builder.build();
    }
}
