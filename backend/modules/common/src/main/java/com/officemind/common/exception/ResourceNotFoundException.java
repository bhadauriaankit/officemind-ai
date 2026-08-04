package com.officemind.common.exception;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String resourceType, Object identifier) {
        super("RESOURCE_NOT_FOUND", "%s with identifier '%s' was not found".formatted(resourceType, identifier));
    }
}
