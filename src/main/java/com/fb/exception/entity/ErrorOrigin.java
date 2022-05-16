package com.fb.exception.entity;

/**
 * Enumeration of systems that can be origins of errors.
 */
public enum ErrorOrigin {
    BLANK("blank"), CIB_CLIENT("cib_client");

    private final String system;


    ErrorOrigin(final String system) {
        this.system = system;
    }

    public String getSystem() {
        return system;
    }
}
