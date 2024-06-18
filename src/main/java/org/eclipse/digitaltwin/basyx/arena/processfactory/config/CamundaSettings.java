package org.eclipse.digitaltwin.basyx.arena.processfactory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "camunda")
public record CamundaSettings(String managedProcessesPath) {
}
