package org.eclipse.digitaltwin.basyx.arena.workermanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

@ConfigurationProperties(prefix = "camunda")
public record CamundaSettings(@Nullable String zeebeGateway) {
}
