package com.camunda.kafka.consumer;

import com.camunda.kafka.dto.ParsedMessage;
import com.camunda.kafka.service.MessageParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MessageConsumer {

    @Autowired
    private MessageParsingService messageParsingService;

    @KafkaListener(topics = "my-topic1", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        log.info("Received message: {}", message);
        
        // Step 2: Extract Process Name & Business Key
        ParsedMessage parsedMessage = messageParsingService.parseMessage(message);
        
        if (parsedMessage.isValid()) {
            log.info("Successfully parsed message - Process Name: {}, Business Key: {}", 
                    parsedMessage.getProcessName(), parsedMessage.getBusinessKey());
            
            // TODO: Step 3 - Process the workflow request
            processWorkflowRequest(parsedMessage);
        } else {
            log.error("Failed to parse message: {}", parsedMessage.getErrorMessage());
            // TODO: Handle parsing error (e.g., send to dead letter queue)
        }
    }
    
    private void processWorkflowRequest(ParsedMessage parsedMessage) {
        // Placeholder for Step 3 implementation
        log.info("Processing workflow request for process: {} with business key: {}", 
                parsedMessage.getProcessName(), parsedMessage.getBusinessKey());
        
        // This is where you would:
        // 1. Validate the process name exists
        // 2. Start the Camunda workflow
        // 3. Handle any errors
    }

}