package com.fb.exception.entity;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.logging.MDC;
import com.fb.HttpCustomHeaders;

import static java.text.MessageFormat.format;

/**
 * Error response returned to caller.
 */
@Schema(name = "ErrorResponse")
public class ErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final String requestId;

    /**
     * Standard constructor for a simple error response.
     *
     * @param errorCode        code transfer to the caller for communication with support
     * @param messageParameter parameters for error message to transport to the caller
     */
    public ErrorResponse(final ErrorCode errorCode, final Object... messageParameter) {
        this.errorCode = errorCode.getErrorOrigin().getSystem() + "_" + errorCode.getCode();
        this.errorMessage =
                messageParameter == null ? errorCode.getMessage() : format(errorCode.getMessage(), messageParameter);
        this.requestId = (String) MDC.get(HttpCustomHeaders.REQUEST_ID);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return "{" + "errorCode='" + errorCode + '\'' + ", errorMessage='" + errorMessage + '\'' + ", requestId='" +
                requestId + '\'' + '}';
    }
}
