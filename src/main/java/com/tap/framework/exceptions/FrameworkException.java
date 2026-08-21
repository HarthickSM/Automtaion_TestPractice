package com.tap.framework.exceptions;

/** Unchecked exception used for every unrecoverable framework level failure. */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
