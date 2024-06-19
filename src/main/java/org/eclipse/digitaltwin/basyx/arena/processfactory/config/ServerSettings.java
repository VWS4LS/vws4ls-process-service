package org.eclipse.digitaltwin.basyx.arena.processfactory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "server")
public record ServerSettings(String externalUrl) {
}
