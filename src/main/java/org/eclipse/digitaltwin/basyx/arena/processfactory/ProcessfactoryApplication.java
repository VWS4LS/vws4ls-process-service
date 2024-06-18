package org.eclipse.digitaltwin.basyx.arena.processfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.eclipse.digitaltwin.basyx.arena.processfactory")
public class ProcessfactoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessfactoryApplication.class, args);
	}

}
