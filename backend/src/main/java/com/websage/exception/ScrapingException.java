package com.websage.exception;

public class ScrapingException extends RuntimeException {

    private final int statusCode;

    public ScrapingException(String message) {
        super(message);
        this.statusCode = 422;
    }

    public ScrapingException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ScrapingException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 502;
    }

    public ScrapingException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
