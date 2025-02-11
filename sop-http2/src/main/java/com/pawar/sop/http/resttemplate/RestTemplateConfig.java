package com.pawar.sop.http.resttemplate;

import java.util.Collections;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.pawar.sop.http.logginginterceptor.LoggingInterceptor;

import okhttp3.logging.HttpLoggingInterceptor;

@Configuration
public class RestTemplateConfig {
	@Bean
	public RestTemplate restTemplate(ClientHttpRequestInterceptor loggingInterceptor) {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(100);
		connectionManager.setDefaultMaxPerRoute(20);

		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(15000)
				.setConnectionRequestTimeout(3000).build();

		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager)
				.setDefaultRequestConfig(requestConfig).build();

		RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

		// Add logging interceptor
		restTemplate.setInterceptors(Collections.singletonList(loggingInterceptor));

		return restTemplate;
	}

	@Bean
	public ClientHttpRequestInterceptor loggingInterceptor() {
		return (ClientHttpRequestInterceptor) new LoggingInterceptor();
	}
}
