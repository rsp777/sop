package com.pawar.sop.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class RestClientException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public RestClientException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

	public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
