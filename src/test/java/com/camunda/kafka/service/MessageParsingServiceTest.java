package com.camunda.kafka.service;

import com.camunda.kafka.dto.ParsedMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageParsingServiceTest {

    private MessageParsingService messageParsingService;

    @BeforeEach
    void setUp() {
        messageParsingService = new MessageParsingService();
    }

    @Test
    void testParseJsonMessage_Success() {
        String jsonMessage = """
            {
                "processName": "order-process",
                "businessKey": "ORDER-12345",
                "variables": {
                    "customerId": "CUST-001",
                    "amount": 100.50
                }
            }
            """;

        ParsedMessage result = messageParsingService.parseMessage(jsonMessage);

        assertTrue(result.isValid());
        assertEquals("order-process", result.getProcessName());
        assertEquals("ORDER-12345", result.getBusinessKey());
        assertNotNull(result.getVariables());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testParseJsonMessage_MissingProcessName() {
        String jsonMessage = """
            {
                "businessKey": "ORDER-12345",
                "variables": {}
            }
            """;

        ParsedMessage result = messageParsingService.parseMessage(jsonMessage);

        assertFalse(result.isValid());
        assertEquals("Process name is required", result.getErrorMessage());
    }

    @Test
    void testParseJsonMessage_MissingBusinessKey() {
        String jsonMessage = """
            {
                "processName": "order-process",
                "variables": {}
            }
            """;

        ParsedMessage result = messageParsingService.parseMessage(jsonMessage);

        assertFalse(result.isValid());
        assertEquals("Business key is required", result.getErrorMessage());
    }

    @Test
    void testParsePlainTextMessage_Success() {
        String plainMessage = "order-process:ORDER-12345:some-variables";

        ParsedMessage result = messageParsingService.parseMessage(plainMessage);

        assertTrue(result.isValid());
        assertEquals("order-process", result.getProcessName());
        assertEquals("ORDER-12345", result.getBusinessKey());
        assertEquals("some-variables", result.getVariables());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testParsePlainTextMessage_MinimalFormat() {
        String plainMessage = "order-process:ORDER-12345";

        ParsedMessage result = messageParsingService.parseMessage(plainMessage);

        assertTrue(result.isValid());
        assertEquals("order-process", result.getProcessName());
        assertEquals("ORDER-12345", result.getBusinessKey());
        assertNull(result.getVariables());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testParsePlainTextMessage_InvalidFormat() {
        String plainMessage = "order-process";

        ParsedMessage result = messageParsingService.parseMessage(plainMessage);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("must contain at least processName and businessKey"));
    }

    @Test
    void testParseMessage_NullMessage() {
        ParsedMessage result = messageParsingService.parseMessage(null);

        assertFalse(result.isValid());
        assertEquals("Message is null or empty", result.getErrorMessage());
    }

    @Test
    void testParseMessage_EmptyMessage() {
        ParsedMessage result = messageParsingService.parseMessage("");

        assertFalse(result.isValid());
        assertEquals("Message is null or empty", result.getErrorMessage());
    }

    @Test
    void testParseMessage_InvalidJson() {
        String invalidJson = "{ invalid json }";

        ParsedMessage result = messageParsingService.parseMessage(invalidJson);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Invalid JSON format"));
    }
}