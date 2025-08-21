# Message Parsing - Step 2 Implementation

This document describes the implementation of Step 2: Extract Process Name & Business Key from incoming messages.

## Overview

The message parsing functionality supports both JSON and plain text message formats and extracts:
- **Process Name**: The name of the Camunda process to start
- **Business Key**: A unique identifier for the workflow instance
- **Variables**: Optional process variables (JSON object or string)

## Supported Message Formats

### 1. JSON Format (Recommended)

```json
{
    "processName": "order-process",
    "businessKey": "ORDER-12345",
    "variables": {
        "customerId": "CUST-001",
        "amount": 100.50,
        "priority": "HIGH"
    }
}
```

**Required Fields:**
- `processName`: String - Name of the process to start
- `businessKey`: String - Unique business identifier

**Optional Fields:**
- `variables`: Object - Process variables to pass to the workflow

### 2. Plain Text Format

Format: `processName:businessKey:variables`

Examples:
- `order-process:ORDER-12345` (minimal)
- `order-process:ORDER-12345:priority=HIGH,amount=100.50` (with variables)

## API Endpoints

### 1. Start Workflow (Raw Message)
```
POST /kafka/workflow/start
Content-Type: text/plain or application/json

Body: (JSON string or plain text)
```

### 2. Start Workflow (Typed)
```
POST /kafka/workflow/start-typed
Content-Type: application/json

Body: WorkflowRequest object
```

## Kafka Consumer

The Kafka consumer automatically processes messages from the `my-topic1` topic and:
1. Parses the message to extract process name and business key
2. Validates the required fields
3. Logs the results
4. Prepares for Step 3 (workflow execution)

## Error Handling

The parsing service returns a `ParsedMessage` object with:
- `isValid()`: Boolean indicating if parsing was successful
- `getErrorMessage()`: Error description if parsing failed
- `getProcessName()`, `getBusinessKey()`, `getVariables()`: Extracted values

Common error scenarios:
- Null or empty message
- Missing required fields (processName, businessKey)
- Invalid JSON format
- Insufficient parts in plain text format

## Testing

Run the unit tests to verify parsing functionality:

```bash
mvn test -Dtest=MessageParsingServiceTest
```

## Example Usage

### Send JSON message via API:
```bash
curl -X POST http://localhost:8080/kafka/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "processName": "order-process",
    "businessKey": "ORDER-12345",
    "variables": {
      "customerId": "CUST-001",
      "amount": 100.50
    }
  }'
```

### Send plain text message via API:
```bash
curl -X POST http://localhost:8080/kafka/workflow/start \
  -H "Content-Type: text/plain" \
  -d "order-process:ORDER-12345:priority=HIGH"
```

### Send typed request:
```bash
curl -X POST http://localhost:8080/kafka/workflow/start-typed \
  -H "Content-Type: application/json" \
  -d '{
    "processName": "order-process",
    "businessKey": "ORDER-12345",
    "variables": {
      "customerId": "CUST-001"
    }
  }'
```

## Next Steps

After successful parsing (Step 2), the next implementation step would be:
- **Step 3**: Process validation and Camunda workflow initiation
- **Step 4**: Error handling and response management
- **Step 5**: Monitoring and logging enhancements