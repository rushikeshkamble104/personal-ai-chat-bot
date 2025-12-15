# Enterprise AI ChatBot with Multi-Assistant Architecture

A sophisticated AI chatbot system built with Spring Boot and LangChain4J, featuring multiple specialized assistants for different use cases including portfolio API integration and PDF operations.

## 🚀 Features

### Multi-Assistant Architecture
- **General Chat Assistant**: Handles general conversations and questions
- **Portfolio API Assistant**: Helps users interact with portfolio APIs for document processing
- **PDF Operations Assistant**: Specialized for PDF file operations (unlock, merge, split, compress, watermark)
- **Code Helper Assistant**: Assists with programming and development questions

### Enterprise-Grade Features
- ✅ **Security**: Spring Security with authentication and authorization
- ✅ **Monitoring**: Prometheus metrics and health checks
- ✅ **Observability**: Distributed tracing with Micrometer
- ✅ **Configuration**: Externalized configuration with profiles
- ✅ **Error Handling**: Global exception handling and proper error responses
- ✅ **Validation**: Request validation with detailed error messages
- ✅ **Documentation**: OpenAPI/Swagger documentation
- ✅ **Containerization**: Docker support with multi-stage builds
- ✅ **Testing**: Comprehensive unit and integration tests

### API Integration
- **Portfolio APIs**: Seamless integration with external document processing APIs
- **File Operations**: Support for file uploads and processing
- **Real-time Processing**: Asynchronous message processing
- **Rate Limiting**: Built-in rate limiting for API protection

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Client App    │────│  ChatBot API     │────│  Assistant      │
│                 │    │  (Spring Boot)   │    │  Services       │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │                        │
                                │                        │
                       ┌──────────────────┐    ┌─────────────────┐
                       │  Portfolio APIs  │    │  LangChain4J    │
                       │  Integration     │    │  (OpenAI)       │
                       └──────────────────┘    └─────────────────┘
```

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.4.1
- **Java Version**: 21
- **AI Integration**: LangChain4J 0.36.2
- **Security**: Spring Security
- **Monitoring**: Micrometer + Prometheus
- **Database**: MapDB (for chat memory)
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, TestContainers

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.8+
- Docker (optional, for containerized deployment)
- OpenAI API Key

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd personal-ai-chat-bot
```

### 2. Configure Environment Variables
```bash
cp .env.example .env
# Edit .env with your configuration
```

Required environment variables:
```bash
OPENAI_API_KEY=your_openai_api_key_here
SECURITY_USER_NAME=admin
SECURITY_USER_PASSWORD=your_secure_password
PORTFOLIO_API_BASE_URL=http://localhost:8081
PORTFOLIO_API_KEY=your_portfolio_api_key
```

### 3. Run with Maven
```bash
./mvnw spring-boot:run
```

### 4. Run with Docker Compose
```bash
docker-compose up -d
```

The application will be available at:
- **API**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/prometheus
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090

## 📚 API Documentation

### Authentication
All endpoints require HTTP Basic Authentication:
```bash
curl -u admin:password http://localhost:8080/api/v1/chat/health
```

### Available Endpoints

#### Chat Operations
```bash
# Send a message to general chat assistant
POST /api/v1/chat/message
{
  "message": "Hello, how can you help me?",
  "assistantType": "GENERAL_CHAT",
  "sessionId": "user-session-123"
}

# Send a message to portfolio API assistant
POST /api/v1/chat/message
{
  "message": "I need to unlock a PDF file",
  "assistantType": "PORTFOLIO_API",
  "sessionId": "user-session-123"
}

# Process a PDF operation
POST /api/v1/chat/message
{
  "message": "Unlock this PDF",
  "assistantType": "PDF_OPERATIONS",
  "sessionId": "user-session-123",
  "operation": "unlock",
  "parameters": {
    "password": "secret123"
  },
  "fileBase64": "base64-encoded-pdf-content",
  "fileName": "document.pdf"
}
```

#### Assistant Management
```bash
# Get available assistants
GET /api/v1/chat/assistants

# Clear chat history
DELETE /api/v1/chat/history

# Clear history for specific assistant
DELETE /api/v1/chat/history?assistantType=pdf-ops
```

### Assistant Types

| Assistant Type | Code | Capabilities |
|---------------|------|-------------|
| General Chat | `GENERAL_CHAT` | General conversations, routing |
| Portfolio API | `PORTFOLIO_API` | API documentation, file operations |
| PDF Operations | `PDF_OPERATIONS` | PDF processing, file manipulation |
| Code Helper | `CODE_HELPER` | Programming assistance |

## 🔧 Configuration

### Application Profiles

#### Development (`dev`)
```yaml
spring:
  profiles:
    active: dev
logging:
  level:
    com.enterprise.chatbot: DEBUG
```

#### Production (`prod`)
```yaml
spring:
  profiles:
    active: prod
  security:
    require-ssl: true
logging:
  level:
    root: WARN
```

### Portfolio API Integration

Configure your portfolio APIs in `application.yml`:
```yaml
app:
  portfolio:
    api:
      base-url: http://your-portfolio-api:8081
      key: your-api-key
      timeout: 30s
      retry-attempts: 3
```

## 📊 Monitoring & Observability

### Health Checks
```bash
curl http://localhost:8080/actuator/health
```

### Metrics
```bash
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus
```

### Custom Metrics
- `chatbot.messages.total` - Total messages processed
- `chatbot.errors.total` - Total errors encountered
- `chat.message.processing` - Message processing time
- `chat.history.clear` - History clear operations

## 🧪 Testing

### Run Tests
```bash
./mvnw test
```

### Run with Coverage
```bash
./mvnw test jacoco:report
```

Coverage reports are generated in `target/site/jacoco/index.html`

## 🐳 Docker Deployment

### Build Image
```bash
docker build -t enterprise-ai-chatbot:1.0.0 .
```

### Run Container
```bash
docker run -d \
  -p 8080:8080 \
  -e OPENAI_API_KEY=your_key \
  -e SPRING_PROFILES_ACTIVE=prod \
  --name chatbot \
  enterprise-ai-chatbot:1.0.0
```

### Docker Compose (Recommended)
```bash
docker-compose up -d
```

This includes:
- ChatBot application
- Prometheus monitoring
- Grafana dashboards
- Persistent volumes for data

## 🔒 Security

### Authentication
- HTTP Basic Authentication
- Configurable user credentials
- Role-based access control

### API Security
- CORS configuration
- Rate limiting
- Input validation
- Error message sanitization

### Best Practices
- Non-root container user
- Secrets management via environment variables
- HTTPS support in production
- Security headers

## 📈 Performance

### Optimization Features
- Asynchronous message processing
- Connection pooling
- Caching for frequently accessed data
- Efficient memory management

### Scaling Considerations
- Stateless design for horizontal scaling
- External session storage support
- Load balancer friendly
- Health check endpoints

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

### Common Issues

#### OpenAI API Key Issues
```bash
# Check if API key is set
echo $OPENAI_API_KEY

# Test API connectivity
curl -H "Authorization: Bearer $OPENAI_API_KEY" \
  https://api.openai.com/v1/models
```

#### Portfolio API Connection Issues
```bash
# Check portfolio API health
curl http://your-portfolio-api:8081/actuator/health

# Test API connectivity
curl -H "X-API-Key: your-key" \
  http://your-portfolio-api:8081/api/pdf/info
```

### Getting Help
- Check the [Issues](../../issues) page
- Review application logs: `docker-compose logs chatbot`
- Monitor health endpoints: `/actuator/health`

## 🗺️ Roadmap

- [ ] WebSocket support for real-time chat
- [ ] Multi-language support
- [ ] Advanced file processing capabilities
- [ ] Integration with more AI providers
- [ ] Enhanced monitoring dashboards
- [ ] Mobile app support
- [ ] Voice interaction capabilities

---

**Built with ❤️ using Spring Boot and LangChain4J**