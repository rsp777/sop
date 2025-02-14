package com.pawar.sop.http.logginginterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
	private static final int MAX_BODY_LOG_LENGTH = 1000;

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		logRequest(request, body);
		long startTime = System.currentTimeMillis();

		try {
			ClientHttpResponse response = execution.execute(request, body);
			logResponse(response, System.currentTimeMillis() - startTime);
			return response;
		} catch (IOException ex) {
			logger.error("Request failed: {} {}", request.getMethod(), request.getURI(), ex);
			throw ex;
		}
	}

	private void logRequest(HttpRequest request, byte[] body) {
		if (logger.isDebugEnabled()) {
			logger.debug("Request: {} {}", request.getMethod(), request.getURI());
			logger.debug("Headers: {}", request.getHeaders());
			logger.debug("Body: {}", truncateBody(new String(body, StandardCharsets.UTF_8)));
		}
	}

	private void logResponse(ClientHttpResponse response, long latency) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Response: {} {}", response.getStatusCode(), response.getStatusText());
			logger.debug("Headers: {}", response.getHeaders());

			String body = truncateBody(StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));
			logger.debug("Body: {}", body);
		}

		// logger.info("HTTP {} → {} | Status: {} | Latency: {}ms", response.getStatusCode().value(),
		// 		response.getStatusCode().getReasonPhrase(), latency);

				logger.info("HTTP {} → {} | Latency: {}ms", response.getStatusCode().value(),
				 latency);
	}

	private String truncateBody(String body) {
		if (body.length() > MAX_BODY_LOG_LENGTH) {
			return body.substring(0, MAX_BODY_LOG_LENGTH) + "... [TRUNCATED]";
		}
		return body;
	}
}
