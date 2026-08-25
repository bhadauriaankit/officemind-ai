package com.officemind.common.exception;

public class ConflictException extends ApplicationException {
    public ConflictException(String message) {
        super("CONFLICT", message);
    }
}
