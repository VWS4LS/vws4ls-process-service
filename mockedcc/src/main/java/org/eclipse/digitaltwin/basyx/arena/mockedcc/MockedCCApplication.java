package org.eclipse.digitaltwin.basyx.arena.mockedcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class,
		OAuth2ResourceServerAutoConfiguration.class })
@ComponentScan(basePackages = "org.eclipse.digitaltwin.basyx.arena.mockedcc")
public class MockedCCApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockedCCApplication.class, args);
	}

}
