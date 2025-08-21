package com.camunda.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowRequest {
    private String processName;
    private String businessKey;
    private Object variables; // Can be Map<String, Object> or any JSON structure
}