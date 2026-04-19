# The Intelligence Hub - Technical Support Co-Pilot

A complete Spring Boot application demonstrating advanced AI integration with hybrid data sources (SQL + Vector Database).

## Project Overview

The Intelligence Hub is an intelligent agent system that:
- **Combines structured data** (SQL database) with **unstructured data** (PDF documents)
- **Autonomously selects tools** based on user queries (Function Calling)
- **Synthesizes responses** from multiple sources using LLMs
- **Follows production-grade architecture** patterns

### Use Case: Technical Support Co-Pilot

When a customer asks: "I have order ORD-2026-001 and my wireless headphones won't connect. Can you check my order status and help troubleshoot?"

The system:
1. Analyzes the query
2. Identifies needed tools: `findOrderStatus` + `getTroubleshootingGuide`
3. Executes both tools in parallel
4. Synthesizes responses into a coherent answer with order status + troubleshooting steps

## Architecture

```
┌─────────────────────────────────────┐
│         REST API Layer              │
│  POST /api/chat                     │
│  POST /api/documents/upload         │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  Intelligence Agent Service         │
│  - Analyze queries                  │
│  - Select tools                     │
│  - Execute tools                    │
│  - Generate responses               │
└──────────────┬──────────────────────┘
               │
       ┌───────┴────────┐
       │                │
┌──────▼─────────┐  ┌──▼──────────────┐
│ SQL Database   │  │ Vector Database  │
│ • Orders       │  │ • Embeddings     │
│ • Customers    │  │ • Documents      │
│ • Products     │  │ • Manuals        │
└────────────────┘  └─────────────────┘
```

## Tech Stack

- **Framework**: Spring Boot 3.5.10
- **Java**: Java 21
- **AI/ML**: Spring AI 1.1.2, OpenAI API
- **Database**: PostgreSQL + pgvector
- **Document Processing**: PDF, DOCX, TXT
- **Build**: Gradle

## Prerequisites

- Java 21+
- PostgreSQL 13+ (with pgvector extension)
- OpenAI API key
- Gradle 8.0+

## Setup Instructions

### 1. Database Setup

```bash
# Create database
createdb intelligence_hub

# Enable pgvector extension
psql intelligence_hub -c "CREATE EXTENSION IF NOT EXISTS vector;"
```

### 2. Environment Variables

```bash
export OPENAI_API_KEY="sk-proj-your-key-here"
```

### 3. Application Properties

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/intelligence_hub
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.ai.openai.api-key=${OPENAI_API_KEY}
```

### 4. Run Application

```bash
./gradlew bootRun
```

Application starts on `http://localhost:8080`

## API Endpoints

### Chat Endpoint

**POST** `/api/chat`

Request:
```json
{
  "query": "I have order ORD-2026-001 and my headphones won't connect via Bluetooth"
}
```

Response:
```json
{
  "success": true,
  "response": "Your order ORD-2026-001 was shipped on April 19...",
  "timestamp": "2026-04-19T10:30:00"
}
```

### Document Upload Endpoint

**POST** `/api/documents/upload`

Parameters:
- `file` (MultipartFile): PDF, DOCX, TXT (max 10MB)
- `productId` (String): Associated product ID

Response:
```json
{
  "success": true,
  "message": "Document processed successfully",
  "filename": "manual.pdf",
  "chunksCreated": 15,
  "productId": "1",
  "timestamp": "2026-04-19T10:30:00"
}
```

### Health Check Endpoints

- `GET /api/chat/health` - Chat service status
- `GET /api/documents/health` - Document service status

## Project Structure

```
IntelligenceHub/
├── src/main/java/com/intelligencehub/
│   ├── config/
│   │   └── DocumentProcessingConfig.java
│   ├── controller/
│   │   ├── ChatController.java
│   │   └── DocumentController.java
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── Customer.java
│   │   │   ├── Product.java
│   │   │   ├── Order.java
│   │   │   └── OrderStatus.java
│   │   └── repository/
│   │       ├── CustomerRepository.java
│   │       ├── ProductRepository.java
│   │       ├── OrderRepository.java
│   │       └── TroubleshootingGuideRepository.java
│   ├── dto/
│   │   ├── request/
│   │   │   └── ChatRequest.java
│   │   └── response/
│   │       ├── ChatResponse.java
│   │       └── OrderStatusResponse.java
│   ├── service/
│   │   ├── agent/
│   │   │   └── IntelligenceAgentService.java
│   │   ├── document/
│   │   │   └── DocumentProcessingService.java
│   │   └── tool/
│   │       ├── DatabaseToolService.java
│   │       └── VectorStoreToolService.java
│   └── IntelligenceHubApplication.java
├── src/main/resources/
│   ├── application.properties
│   └── prompts/
│       └── agent-system-prompt.txt
├── build.gradle
├── settings.gradle
└── README.md
```

## Key Components

### Tools Available

**DatabaseToolService**:
- `findOrderStatus(orderNumber)` - Look up order status
- `getProductSpecifications(productId)` - Get product details
- `searchProducts(productName)` - Search for products

**VectorStoreToolService**:
- `searchProductManual(query, topK)` - Search product documentation
- `getTroubleshootingGuide(productId, issue)` - Get troubleshooting steps

### Data Flow

1. **Query Processing**: Agent analyzes user query
2. **Tool Selection**: Determines which tools are needed
3. **Tool Execution**: Calls appropriate tools with extracted parameters
4. **Result Aggregation**: Collects results from all tools
5. **Response Generation**: LLM synthesizes final response
6. **Response Return**: Returns structured JSON response

## Example Queries

### Example 1: Order + Troubleshooting
```
"I have order ORD-2026-001 and my wireless headphones won't connect via Bluetooth"

Agent Flow:
1. Extract: orderNumber="ORD-2026-001", productId="1", issue="bluetooth"
2. Call: findOrderStatus("ORD-2026-001")
3. Call: getTroubleshootingGuide("1", "bluetooth connection")
4. Synthesize: Order status + troubleshooting steps
```

### Example 2: Product Information
```
"Do you have Wireless Headphones Pro in stock?"

Agent Flow:
1. Extract: productName="Wireless Headphones Pro"
2. Call: searchProducts("Wireless Headphones Pro")
3. Call: getProductSpecifications("1")
4. Return: Availability and specs
```

## Testing

### Manual Testing

```bash
# Test chat endpoint
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"query": "I have order ORD-2026-001"}'

# Test document upload
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@product-manual.pdf" \
  -F "productId=1"
```

## Performance Considerations

- **Chunk Size**: 800 tokens per chunk (configurable)
- **Chunk Overlap**: 100 tokens (prevents context loss)
- **Vector Dimensions**: 1536 (text-embedding-3-small)
- **Top-K Results**: 3-5 chunks for context (configurable)

## Enhancement Roadmap

### Phase 2: Multi-turn Conversations
- Conversation history persistence
- Context carryover
- Follow-up handling

### Phase 3: Advanced Reasoning
- Self-reflection on responses
- Tool composition (chaining)
- Dynamic tool discovery

### Phase 4: Streaming & UI
- Server-Sent Events
- React frontend
- Real-time response delivery

### Phase 5: Analytics
- Query metrics
- Response quality tracking
- Fine-tuning pipeline

## Production Deployment

### Docker

```dockerfile
FROM openjdk:21-slim
WORKDIR /app
COPY build/libs/IntelligenceHub-1.0.0.jar app.jar
ENV OPENAI_API_KEY=${OPENAI_API_KEY}
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Kubernetes

See `kubernetes/` directory for deployment manifests.

## Monitoring & Logging

- Logs: INFO level for business logic, DEBUG for agent reasoning
- Metrics: Response time, tool usage, error rates
- Tracing: Each query gets a unique trace ID

## Error Handling

- Empty queries → 400 Bad Request
- Unsupported file types → 400 Bad Request
- Processing errors → 500 Internal Server Error
- Missing data → Graceful fallback with appropriate messages

## Success Criteria Met

✅ **Hybrid Knowledge** - Combines SQL + PDF data  
✅ **Autonomous Reasoning** - AI selects tools dynamically  
✅ **Function Calling** - Tools called based on query analysis  
✅ **Clean Architecture** - Controller → Service → Repository pattern  
✅ **Production Ready** - Error handling, logging, validation  

## Troubleshooting

### Issue: Vector dimension mismatch
```
Solution: Ensure embedding model matches vector dimensions
- text-embedding-3-small = 1536
- Set in application.properties
```

### Issue: API key errors
```
Solution:
1. Verify OPENAI_API_KEY environment variable is set
2. Key should start with "sk-proj-"
3. Check key has correct permissions
```

### Issue: Database connection refused
```
Solution:
1. Ensure PostgreSQL is running
2. Verify credentials in application.properties
3. Check database exists: createdb intelligence_hub
```

## License

This project is provided as-is for educational and commercial use.

## Support

For questions or issues:
1. Check troubleshooting section
2. Review logs for error details
3. Verify all prerequisites are installed

---

**Built with Spring Boot, Spring AI, and modern AI/ML practices**
