package com.pawar.sop.http.exception;

import org.springframework.http.HttpStatus;

public class RestClientException extends RuntimeException {
    private final HttpStatus statusCode;

    public RestClientException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
