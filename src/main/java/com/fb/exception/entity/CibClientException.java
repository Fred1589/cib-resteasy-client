package com.fb.exception.entity;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static java.text.MessageFormat.format;

/**
 * Basic exception with standard functionality
 * <p>
 * Extends WebApplicationExceptions and gets therefore automatically converted to HTTP response!
 */
public abstract class CibClientException extends WebApplicationException {

    private static final long serialVersionUID = 415167940658538702L;

    public static final int LENGTH_BEFORE_CUT = 10;

    private final Response.Status status;
    private final ErrorCode errorCode;
    private final Object[] messageParameters;


    /**
     * Standard constructor for simple message based exceptions with root rootCause.
     *
     * @param status           response status to reflect in the REST-API
     * @param errorCode        error code to transport to the caller
     * @param rootCause        root cause exception
     * @param messageParameter parameters for error message to transport to the caller
     */
    public CibClientException(final Response.Status status, final ErrorCode errorCode, final Throwable rootCause,
            final Object... messageParameter) {
        super(rootCause, Response.status(status).entity(new ErrorResponse(errorCode, messageParameter)).build());
        this.status = status;
        this.errorCode = errorCode;
        this.messageParameters = messageParameter == null ? null : messageParameter.clone();
    }

    /**
     * This can be used to log a shorter stacktrace with only the relevant parts from start and the last "Caused by"
     *
     * @return the reduced (cut out in the middle) String version of the stacktrace
     */
    public String shortStackTrace() {
        final String stacktrace = stacktrace2String();
        final String[] lines = stacktrace.split("\\r?\\n");
        final StringBuilder buffer = new StringBuilder();

        // reduce only for stacktraces larger 40 lines
        if (lines.length > 40) {
            // add the first 10 lines of the stacktrace
            for (int i = 0; i < LENGTH_BEFORE_CUT; i++) {
                buffer.append(lines[i]).append(System.lineSeparator());
            }

            // find last "Caused by:" and add from there to the end
            int e = lines.length - 1;
            while (!lines[e].startsWith("Caused By:") && e > 10) {
                e--;
            }

            if (e != LENGTH_BEFORE_CUT) {
                buffer.append(
                        "... reduced ... reduced ... reduced ... reduced ... reduced ... reduced ... reduced ..." +
                                System.lineSeparator());
            }

            for (int i = e; i < lines.length; i++) {
                buffer.append(lines[i]).append(System.lineSeparator());
            }
        } else {
            return stacktrace;
        }

        return buffer.toString();
    }

    /**
     * Provide the full stacktrace of this exception as String.
     *
     * @return a String with the full stacktrace
     */
    public String stacktrace2String() {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        this.printStackTrace(new PrintStream(stream, false, StandardCharsets.UTF_8));
        return stream.toString(StandardCharsets.UTF_8);
    }

    public Response.Status getStatus() {
        return status;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return messageParameters == null ? errorCode.getMessage() : format(errorCode.getMessage(), messageParameters);
    }
}
