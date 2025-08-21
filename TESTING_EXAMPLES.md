# Testing Examples for Step 2: Message Parsing

This document provides practical examples for testing the message parsing functionality.

## Prerequisites

1. Start the application:
```bash
mvn spring-boot:run
```

2. The application will be available at `http://localhost:8080`

## Test Examples

### 1. Test JSON Message via API

**Valid JSON Request:**
```bash
curl -X POST http://localhost:8080/kafka/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "processName": "order-process",
    "businessKey": "ORDER-12345",
    "variables": {
      "customerId": "CUST-001",
      "amount": 100.50,
      "priority": "HIGH"
    }
  }'
```

**Expected Response:**
```
Workflow request processed successfully - Process: order-process, Business Key: ORDER-12345
```

**Invalid JSON Request (Missing Business Key):**
```bash
curl -X POST http://localhost:8080/kafka/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "processName": "order-process",
    "variables": {
      "customerId": "CUST-001"
    }
  }'
```

**Expected Response:**
```
Error: Business key is required
```

### 2. Test Plain Text Message via API

**Valid Plain Text Request:**
```bash
curl -X POST http://localhost:8080/kafka/workflow/start \
  -H "Content-Type: text/plain" \
  -d "order-process:ORDER-12345:priority=HIGH,amount=100.50"
```

**Expected Response:**
```
Workflow request processed successfully - Process: order-process, Business Key: ORDER-12345
```

**Minimal Plain Text Request:**
```bash
curl -X POST http://localhost:8080/kafka/workflow/start \
  -H "Content-Type: text/plain" \
  -d "order-process:ORDER-12345"
```

**Invalid Plain Text Request:**
```bash
curl -X POST http://localhost:8080/kafka/workflow/start \
  -H "Content-Type: text/plain" \
  -d "order-process"
```

**Expected Response:**
```
Error: Plain text message must contain at least processName and businessKey separated by ':'
```

### 3. Test Typed API Endpoint

**Valid Typed Request:**
```bash
curl -X POST http://localhost:8080/kafka/workflow/start-typed \
  -H "Content-Type: application/json" \
  -d '{
    "processName": "payment-process",
    "businessKey": "PAY-67890",
    "variables": {
      "amount": 250.75,
      "currency": "USD"
    }
  }'
```

### 4. Test Kafka Message (via Producer API)

**Send message to Kafka topic:**
```bash
curl -X POST "http://localhost:8080/kafka/send?message={\"processName\":\"order-process\",\"businessKey\":\"ORDER-12345\",\"variables\":{\"customerId\":\"CUST-001\"}}"
```

This will send the message to the Kafka topic, and you should see the parsing results in the application logs.

### 5. Monitor Application Logs

When testing, monitor the application logs to see the parsing results:

**Successful parsing log:**
```
INFO  c.c.k.service.MessageParsingService - Successfully parsed JSON message - Process: order-process, Business Key: ORDER-12345
INFO  c.c.kafka.consumer.MessageConsumer - Successfully parsed message - Process Name: order-process, Business Key: ORDER-12345
```

**Failed parsing log:**
```
ERROR c.c.kafka.consumer.MessageConsumer - Failed to parse message: Business key is required
```

## Test Data Variations

### Different Process Types
```json
{
  "processName": "user-registration",
  "businessKey": "USER-001",
  "variables": {
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

```json
{
  "processName": "invoice-processing",
  "businessKey": "INV-2024-001",
  "variables": {
    "vendorId": "VENDOR-123",
    "amount": 1500.00,
    "dueDate": "2024-12-31"
  }
}
```

### Edge Cases

**Empty variables:**
```json
{
  "processName": "simple-process",
  "businessKey": "SIMPLE-001",
  "variables": {}
}
```

**No variables:**
```json
{
  "processName": "minimal-process",
  "businessKey": "MIN-001"
}
```

**Complex variables:**
```json
{
  "processName": "complex-process",
  "businessKey": "COMPLEX-001",
  "variables": {
    "customer": {
      "id": "CUST-001",
      "name": "John Doe",
      "address": {
        "street": "123 Main St",
        "city": "Anytown",
        "zipCode": "12345"
      }
    },
    "items": [
      {"id": "ITEM-001", "quantity": 2, "price": 50.00},
      {"id": "ITEM-002", "quantity": 1, "price": 25.00}
    ],
    "metadata": {
      "source": "web",
      "timestamp": "2024-01-15T10:30:00Z"
    }
  }
}
```

## Automated Testing

Run the unit tests:
```bash
mvn test -Dtest=MessageParsingServiceTest
```

Run all tests:
```bash
mvn test
```