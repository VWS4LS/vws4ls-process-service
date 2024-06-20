package org.eclipse.digitaltwin.basyx.arena.processfactory.config;

import java.net.URI;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;

@Configuration
@EnableConfigurationProperties({
                CamundaSettings.class
})
public class CamundaConfiguration {

        @Bean
        public ZeebeClient getZeebeClient(CamundaSettings settings) {
                ZeebeClientBuilder builder = ZeebeClient.newClientBuilder().usePlaintext();

                if (settings.zeebeGateway() != null && !settings.zeebeGateway().isBlank())
                        builder.grpcAddress(URI.create(settings.zeebeGateway()));

                return builder.build();
        }

}
