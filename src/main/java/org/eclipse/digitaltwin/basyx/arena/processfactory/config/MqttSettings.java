package org.eclipse.digitaltwin.basyx.arena.processfactory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Primary
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public record MqttSettings(
        String hostname,
        int port,
        String username,
        String password,
        String topic) {
}