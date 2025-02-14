package com.pawar.sop.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class RestClientException extends RuntimeException {
    private final HttpStatusCode httpStatusCode;

    public RestClientException(String message, HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatusCode getStatusCode() {
        return httpStatusCode;
    }
}
