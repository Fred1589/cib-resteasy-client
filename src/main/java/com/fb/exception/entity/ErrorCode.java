package com.fb.exception.entity;

/**
 * Enumeration of codes used to deliver uniform error information to the caller.
 * <p>
 * Prefix error codes with B for business and T for technical exceptions.
 */
public enum ErrorCode {

    UNDEFINED(ErrorOrigin.CIB_CLIENT, "0000", "Undefined error."),

    /* OFCON Business Exception Codes */
    B_INVALID_PARAMETER(ErrorOrigin.CIB_CLIENT, "1000", "Invalid request parameter(s) {0}."),

    // Technical error codes
    T_INTERNAL_SERVER_ERROR(ErrorOrigin.CIB_CLIENT, "2000", "Internal server error"),

    // General external systems error codes
    B_EXT_4XX(ErrorOrigin.BLANK, "3000", "{0} {1} - Response Body {2}"),
    T_EXT_5XX(ErrorOrigin.BLANK, "3001", "{0} {1} - Response Body {2}");

    private final String code;
    private final String message;
    private ErrorOrigin errorOrigin;

    /**
     * Standard constructor for error codes.
     *
     * @param errorOrigin system where error occurred
     * @param code        error code for easy identification
     * @param message     error message for caller
     */
    ErrorCode(final ErrorOrigin errorOrigin, final String code, final String message) {
        this.errorOrigin = errorOrigin;
        this.code = code;
        this.message = message;
    }

    /**
     * Override errorOrigin (e.g. replace default for external systems)
     *
     * @param errorOrigin system where error occurred
     * @return error code with overridden origin
     */
    public ErrorCode overrideOrigin(final ErrorOrigin errorOrigin) {
        this.errorOrigin = errorOrigin;
        return this;
    }

    public ErrorOrigin getErrorOrigin() {
        return errorOrigin;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
