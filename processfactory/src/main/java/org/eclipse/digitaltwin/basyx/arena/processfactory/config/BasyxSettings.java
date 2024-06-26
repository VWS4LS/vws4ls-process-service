package org.eclipse.digitaltwin.basyx.arena.processfactory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "basyx")
public record BasyxSettings(String processSmUrl, String processFileSEIdShort, String operationsSmUrl) {
}
