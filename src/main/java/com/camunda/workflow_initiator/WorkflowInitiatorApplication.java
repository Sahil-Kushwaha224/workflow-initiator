package com.camunda.workflow_initiator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.camunda.kafka")
public class WorkflowInitiatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkflowInitiatorApplication.class, args);
	}

}
