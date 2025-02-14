package com.pawar.sop.http.service;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.pawar.sop.http.exception.RestClientException;
import com.pawar.sop.http.exception.RestTimeoutException;

@Service
public class HttpService {
private static final Logger logger = LoggerFactory.getLogger(HttpService.class);
    
    private final RestTemplate restTemplate;

    public HttpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> ResponseEntity<T> restCall(String url, HttpMethod method, 
                                          Object body, Map<String, Object> queryParams) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            if (queryParams != null) {
                queryParams.forEach((key, value) -> 
                    builder.queryParam(key, String.valueOf(value)));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Object> httpEntity = new HttpEntity<>(body, headers);

            return (ResponseEntity<T>) restTemplate.exchange(
                builder.build().toUri(), 
                method, 
                httpEntity, 
                String.class
            );
            
        } catch (HttpClientErrorException e) {
            logger.error("Client error (4xx) - URL: {} | Status: {} | Response: {}", 
                url, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RestClientException("Client error: " + e.getMessage(), e.getStatusCode());
            
        } catch (HttpServerErrorException e) {
            logger.error("Server error (5xx) - URL: {} | Status: {} | Response: {}", 
                url, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RestClientException("Server error: " + e.getMessage(), e.getStatusCode());
            
        } catch (ResourceAccessException e) {
            logger.error("Connection timeout - URL: {}", url, e);
            throw new RestTimeoutException("Service unavailable: " + e.getMessage());
            
        } 
//        catch (URISyntaxException e) {
//            logger.error("Invalid URL: {}", url, e);
//            throw new RestClientException("Invalid URL format", HttpStatus.BAD_REQUEST);
//            
//        } 
        catch (Exception e) {
            logger.error("Unexpected error calling {}: {}", url, e.getMessage(), e);
            throw new RestClientException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
