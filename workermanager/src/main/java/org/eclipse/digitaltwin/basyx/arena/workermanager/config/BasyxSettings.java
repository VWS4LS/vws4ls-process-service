package org.eclipse.digitaltwin.basyx.arena.workermanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "basyx")
public record BasyxSettings(String aasRepositoryUrl, String qualifierSkillProvider) {
}
