package com.officemind.common.exception;

/**
 * Root of the OfficeMind AI exception hierarchy.
 *
 * Every domain/application-level failure must extend this class so that a
 * single {@code @ControllerAdvice} in the api module can translate it into a
 * consistent {@code ProblemDetail} (RFC 7807) response without leaking
 * internal stack traces to clients.
 */
public abstract class ApplicationException extends RuntimeException {

    private final String errorCode;

    protected ApplicationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected ApplicationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
