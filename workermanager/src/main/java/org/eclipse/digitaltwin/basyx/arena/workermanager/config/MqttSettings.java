package org.eclipse.digitaltwin.basyx.arena.workermanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mqtt")
public record MqttSettings(
                String hostname,
                int port,
                String username,
                String password,
        String topic,
        String clientId) {
}