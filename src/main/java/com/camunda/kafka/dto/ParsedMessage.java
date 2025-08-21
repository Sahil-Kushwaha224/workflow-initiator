package com.camunda.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParsedMessage {
    private String processName;
    private String businessKey;
    private Object variables;
    private boolean isValid;
    private String errorMessage;
    
    public static ParsedMessage success(String processName, String businessKey, Object variables) {
        return new ParsedMessage(processName, businessKey, variables, true, null);
    }
    
    public static ParsedMessage error(String errorMessage) {
        return new ParsedMessage(null, null, null, false, errorMessage);
    }
}