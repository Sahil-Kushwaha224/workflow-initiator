package com.camunda.kafka.service;

import com.camunda.kafka.dto.ParsedMessage;
import com.camunda.kafka.dto.WorkflowRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageParsingService {
    
    private final ObjectMapper objectMapper;
    
    public MessageParsingService() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Parse incoming message and extract process name and business key
     * Supports both JSON and plain text formats
     */
    public ParsedMessage parseMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return ParsedMessage.error("Message is null or empty");
        }
        
        try {
            // Try to parse as JSON first
            if (message.trim().startsWith("{")) {
                return parseJsonMessage(message);
            } else {
                // Handle plain text format (you can customize this based on your needs)
                return parsePlainTextMessage(message);
            }
        } catch (Exception e) {
            log.error("Error parsing message: {}", message, e);
            return ParsedMessage.error("Failed to parse message: " + e.getMessage());
        }
    }
    
    private ParsedMessage parseJsonMessage(String message) {
        try {
            WorkflowRequest request = objectMapper.readValue(message, WorkflowRequest.class);
            
            // Validate required fields
            if (request.getProcessName() == null || request.getProcessName().trim().isEmpty()) {
                return ParsedMessage.error("Process name is required");
            }
            
            if (request.getBusinessKey() == null || request.getBusinessKey().trim().isEmpty()) {
                return ParsedMessage.error("Business key is required");
            }
            
            log.info("Successfully parsed JSON message - Process: {}, Business Key: {}", 
                    request.getProcessName(), request.getBusinessKey());
            
            return ParsedMessage.success(
                request.getProcessName().trim(),
                request.getBusinessKey().trim(),
                request.getVariables()
            );
            
        } catch (Exception e) {
            log.error("Error parsing JSON message", e);
            return ParsedMessage.error("Invalid JSON format: " + e.getMessage());
        }
    }
    
    private ParsedMessage parsePlainTextMessage(String message) {
        // Example format: "processName:businessKey:variables"
        // You can customize this based on your specific plain text format
        String[] parts = message.split(":", 3);
        
        if (parts.length < 2) {
            return ParsedMessage.error("Plain text message must contain at least processName and businessKey separated by ':'");
        }
        
        String processName = parts[0].trim();
        String businessKey = parts[1].trim();
        Object variables = parts.length > 2 ? parts[2].trim() : null;
        
        if (processName.isEmpty()) {
            return ParsedMessage.error("Process name cannot be empty");
        }
        
        if (businessKey.isEmpty()) {
            return ParsedMessage.error("Business key cannot be empty");
        }
        
        log.info("Successfully parsed plain text message - Process: {}, Business Key: {}", 
                processName, businessKey);
        
        return ParsedMessage.success(processName, businessKey, variables);
    }
}