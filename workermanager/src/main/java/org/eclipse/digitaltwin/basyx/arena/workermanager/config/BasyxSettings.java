package org.eclipse.digitaltwin.basyx.arena.workermanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "basyx")
public record BasyxSettings(String aasRegistryUrl, String aasRepositoryUrl, String submodelRegistryUrl,
        String submodelRepositoryUrl, String qualifierSkillProvider) {
}