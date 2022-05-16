package com.fb.exception.entity;

import javax.ws.rs.core.Response;
import java.io.Serial;


/**
 * Basic business exception. Business exceptions should be a result of invalid business state or e.g. calculation errors.
 * The main feature of business exceptions is that they cannot be resolved by retrying the operation. Normally a
 * manual fix needs to be done.
 * <p>
 * Business exceptions do break a business process! Otherwise, you should not use business exceptions in that situation.
 * <p>
 * Don't use business exceptions to reflect technical errors.
 */
public class CibClientBusinessException extends CibClientException {

    @Serial
    private static final long serialVersionUID = -3625565779900124308L;

    /**
     * Standard constructor for simple message based exceptions.
     *
     * @param errorCode        error code to transport to the caller
     * @param messageParameter parameters for error message to transport to the caller
     */
    public CibClientBusinessException(final ErrorCode errorCode, final Object... messageParameter) {
        super(Response.Status.BAD_REQUEST, errorCode, null, messageParameter);
    }

    /**
     * Standard constructor for simple message based exceptions with root rootCause.
     *
     * @param errorCode        error code to transport to the caller
     * @param rootCause        root cause exception
     * @param messageParameter parameters for error message to transport to the caller
     */
    public CibClientBusinessException(final ErrorCode errorCode, final Throwable rootCause,
            final Object... messageParameter) {
        super(Response.Status.BAD_REQUEST, errorCode, rootCause, messageParameter);
    }

    /**
     * Constructor for message based exceptions with configurable status.
     *
     * @param status           http status to return to caller
     * @param errorCode        error code to transport to the caller
     * @param messageParameter parameters for error message to transport to the caller
     */
    public CibClientBusinessException(final Response.Status status, final ErrorCode errorCode,
            final Object... messageParameter) {
        super(status, errorCode, null, messageParameter);
    }

    /**
     * Constructor for message based exceptions with configurable status and rootCause.
     *
     * @param status           http status to return to caller
     * @param errorCode        error code to transport to the caller
     * @param rootCause        root cause exception
     * @param messageParameter parameters for error message to transport to the caller
     */
    public CibClientBusinessException(final Response.Status status, final ErrorCode errorCode,
            final Throwable rootCause, final Object... messageParameter) {
        super(status, errorCode, rootCause, messageParameter);
    }
}
