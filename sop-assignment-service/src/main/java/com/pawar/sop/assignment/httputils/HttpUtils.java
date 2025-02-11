package com.pawar.sop.assignment.httputils;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class HttpUtils {
	private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	private final HttpClient httpClient;

	public HttpUtils() {
		httpClient = HttpClients.createDefault();

	}

	public ResponseEntity<String> restCall(String url, HttpMethod method, Object body, Map<String, Object> queryParams) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build the URL with query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }

        String finalUrl = builder.toUriString();
        logger.info("finalUrl : {}", finalUrl);

        // Create the HttpEntity with headers and body
        HttpEntity<Object> httpEntity = new HttpEntity<>(body, headers);

        try {
            // Make the HTTP request
            ResponseEntity<String> response = restTemplate.exchange(finalUrl, method, httpEntity, String.class);
            logger.info("HTTP Status Code: {}", response.getStatusCodeValue());
            return response;
        } catch (HttpClientErrorException e) {
            // Handle 4xx errors (e.g., 404 Not Found)
            logger.error("HTTP Client Error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e) {
            // Handle other exceptions (e.g., connection issues)
            logger.error("Error during REST call: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

}
