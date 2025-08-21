package com.camunda.kafka.controller;

import com.camunda.kafka.dto.ParsedMessage;
import com.camunda.kafka.dto.WorkflowRequest;
import com.camunda.kafka.producer.MessageProducer;
import com.camunda.kafka.service.MessageParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/kafka")
@Slf4j
public class KafkaController {

    @Autowired
    private MessageProducer messageProducer;
    
    @Autowired
    private MessageParsingService messageParsingService;

    @PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
        messageProducer.sendMessage("my-topic1", message);
        return "Message sent: " + message;
    }
    
    @PostMapping("/workflow/start")
    public ResponseEntity<?> startWorkflow(@RequestBody String message) {
        log.info("Received workflow start request: {}", message);
        
        // Step 2: Extract Process Name & Business Key
        ParsedMessage parsedMessage = messageParsingService.parseMessage(message);
        
        if (parsedMessage.isValid()) {
            log.info("Successfully parsed workflow request - Process Name: {}, Business Key: {}", 
                    parsedMessage.getProcessName(), parsedMessage.getBusinessKey());
            
            // TODO: Step 3 - Process the workflow request
            // For now, just return success response
            return ResponseEntity.ok().body(String.format(
                "Workflow request processed successfully - Process: %s, Business Key: %s", 
                parsedMessage.getProcessName(), parsedMessage.getBusinessKey()));
        } else {
            log.error("Failed to parse workflow request: {}", parsedMessage.getErrorMessage());
            return ResponseEntity.badRequest().body("Error: " + parsedMessage.getErrorMessage());
        }
    }
    
    @PostMapping("/workflow/start-typed")
    public ResponseEntity<?> startWorkflowTyped(@RequestBody WorkflowRequest request) {
        log.info("Received typed workflow start request - Process: {}, Business Key: {}", 
                request.getProcessName(), request.getBusinessKey());
        
        // Validate required fields
        if (request.getProcessName() == null || request.getProcessName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Process name is required");
        }
        
        if (request.getBusinessKey() == null || request.getBusinessKey().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Business key is required");
        }
        
        // TODO: Step 3 - Process the workflow request
        // For now, just return success response
        return ResponseEntity.ok().body(String.format(
            "Workflow request processed successfully - Process: %s, Business Key: %s", 
            request.getProcessName(), request.getBusinessKey()));
    }

}