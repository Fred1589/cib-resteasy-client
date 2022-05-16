package com.fb.exception.entity;

import javax.ws.rs.core.Response;
import java.io.Serial;


/**
 * Basic technical exception. Technical exceptions should be a result of technical problems like network or I/O errors.
 * The main feature of technical exceptions is that they might be resolved by retrying the operation.
 * Technical exceptions do not necessarily break a business process (like warnings) and have more optional behavior.
 * <p>
 * Don't use technical exceptions to reflect business errors.
 */
public class CibClientTechnicalException extends CibClientException {

    @Serial
    private static final long serialVersionUID = 58288587545327342L;

    /**
     * Standard constructor for simple message based exceptions.
     *
     * @param errorCode        error code to transport to the caller
     * @param messageParameter parameters for error message to transport to the caller
     */
    public CibClientTechnicalException(final ErrorCode errorCode, final Object... messageParameter) {
        super(Response.Status.INTERNAL_SERVER_ERROR, errorCode, null, messageParameter);
    }

    /**
     * Standard constructor for simple message based exceptions with root rootCause.
     *
     * @param errorCode        error code to transport to the caller
     * @param rootCause        root cause exception
     * @param messageParameter parameters for error message to transport to the caller
     */
    public CibClientTechnicalException(final ErrorCode errorCode, final Throwable rootCause,
            final Object... messageParameter) {
        super(Response.Status.INTERNAL_SERVER_ERROR, errorCode, rootCause, messageParameter);
    }
}
