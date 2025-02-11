package com.pawar.sop.http.exception;

import org.springframework.http.HttpStatus;

public class RestTimeoutException extends RestClientException {
    public RestTimeoutException(String message) {
        super(message, HttpStatus.REQUEST_TIMEOUT);
    }
}
