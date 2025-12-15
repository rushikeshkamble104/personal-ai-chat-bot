FROM openjdk:21-jdk-slim

LABEL maintainer="Enterprise AI ChatBot Team"
LABEL version="1.0.0"
LABEL description="Enterprise AI ChatBot with LangChain4J"

# Create app directory
WORKDIR /app

# Create non-root user
RUN groupadd -r chatbot && useradd -r -g chatbot chatbot

# Copy Maven wrapper and pom.xml
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# Create data directory
RUN mkdir -p /app/data && chown -R chatbot:chatbot /app

# Switch to non-root user
USER chatbot

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "target/ai-chatbot-1.0.0.jar"]