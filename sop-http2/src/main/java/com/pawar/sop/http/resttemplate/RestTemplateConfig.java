package com.pawar.sop.http.resttemplate;

import java.sql.Time;
import java.util.Collections;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.pawar.sop.http.config.TimeoutConfig;
import com.pawar.sop.http.logginginterceptor.LoggingInterceptor;

@Configuration
public class RestTemplateConfig {

	
	private final TimeoutConfig timeoutConfig;
	
	
	
	public RestTemplateConfig(TimeoutConfig timeoutConfig) {
		this.timeoutConfig = timeoutConfig;
	}

	@Bean
	public RestTemplate restTemplate(ClientHttpRequestInterceptor loggingInterceptor) {
		// Connection pool setup
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(100);
		connectionManager.setDefaultMaxPerRoute(20);

		// Timeout configuration
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(Timeout.ofSeconds(timeoutConfig.getConnectionTimeout()))
				.setResponseTimeout(Timeout.ofSeconds(timeoutConfig.getResponseTimeout())) // Replaces socketTimeout
				.setConnectionRequestTimeout(Timeout.ofSeconds(timeoutConfig.getConnectionRequestTimeout())).build();

		// HttpClient 5 configuration
		HttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager)
				.setDefaultRequestConfig(requestConfig).build();

		// RestTemplate with HttpClient 5 factory
		RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

		// Add interceptors
		restTemplate.getInterceptors().add(loggingInterceptor);

		return restTemplate;
	}

	@Bean
	public ClientHttpRequestInterceptor loggingInterceptor() {
		return new LoggingInterceptor(); // No cast needed
	}
}
