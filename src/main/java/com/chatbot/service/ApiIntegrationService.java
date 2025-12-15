package com.chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for integrating with external APIs (Portfolio APIs)
 */
@Slf4j
@Service
public class ApiIntegrationService {

    private final RestTemplate restTemplate;
    
    @Value("${app.portfolio.api.base-url:http://localhost:8081}")
    private String portfolioApiBaseUrl;
    
    @Value("${app.portfolio.api.key:}")
    private String portfolioApiKey;

    public ApiIntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Execute a general API operation
     */
    public Map<String, Object> executeApiOperation(String endpoint, String operation, 
                                                 Map<String, Object> parameters, 
                                                 String fileBase64, String fileName) {
        try {
            String fullUrl = portfolioApiBaseUrl + endpoint;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (portfolioApiKey != null && !portfolioApiKey.isEmpty()) {
                headers.set("X-API-Key", portfolioApiKey);
            }

            Map<String, Object> requestBody = new HashMap<>();
            if (parameters != null) {
                requestBody.putAll(parameters);
            }
            
            if (fileBase64 != null) {
                requestBody.put("file", fileBase64);
                requestBody.put("fileName", fileName);
            }
            
            requestBody.put("operation", operation);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                fullUrl, HttpMethod.POST, request, Map.class);

            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("data", response.getBody());
            result.put("httpStatus", response.getStatusCode().value());
            
            System.out.println("API operation completed successfully: " + operation);
            return result;

        } catch (Exception e) {
            System.err.println("API operation failed: " + operation + ", " + e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "error");
            errorResult.put("error", e.getMessage());
            return errorResult;
        }
    }

    /**
     * Execute PDF-specific operations
     */
    public Map<String, Object> executePdfOperation(String operation, Map<String, Object> parameters, 
                                                 String fileBase64, String fileName) {
        String endpoint = "/api/pdf/" + operation;
        return executeApiOperation(endpoint, operation, parameters, fileBase64, fileName);
    }

    /**
     * Execute file conversion operations
     */
    public Map<String, Object> executeConversionOperation(String operation, Map<String, Object> parameters, 
                                                        String fileBase64, String fileName) {
        String endpoint = "/api/convert/" + operation;
        return executeApiOperation(endpoint, operation, parameters, fileBase64, fileName);
    }

    /**
     * Execute document processing operations
     */
    public Map<String, Object> executeDocumentOperation(String operation, Map<String, Object> parameters, 
                                                      String fileBase64, String fileName) {
        String endpoint = "/api/document/" + operation;
        return executeApiOperation(endpoint, operation, parameters, fileBase64, fileName);
    }

    /**
     * Get API documentation
     */
    public Map<String, Object> getApiDocumentation() {
        try {
            String endpoint = "/api/docs";
            String fullUrl = portfolioApiBaseUrl + endpoint;
            
            HttpHeaders headers = new HttpHeaders();
            if (portfolioApiKey != null && !portfolioApiKey.isEmpty()) {
                headers.set("X-API-Key", portfolioApiKey);
            }

            HttpEntity<String> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                fullUrl, HttpMethod.GET, request, Map.class);

            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("documentation", response.getBody());
            
            return result;

        } catch (Exception e) {
            System.err.println("Failed to fetch API documentation: " + e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "error");
            errorResult.put("error", "Failed to fetch API documentation: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * Check API health
     */
    public boolean isApiHealthy() {
        try {
            String healthUrl = portfolioApiBaseUrl + "/actuator/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(healthUrl, Map.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            System.err.println("Portfolio API health check failed: " + e.getMessage());
            return false;
        }
    }
}