package org.eclipse.digitaltwin.basyx.arena.processfactory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;


@ConfigurationProperties(prefix = "camunda")
public record CamundaSettings(String managedProcessesPath, @Nullable String zeebeGateway) {
}
