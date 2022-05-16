package com.fb;

import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import com.fb.exception.entity.CibClientBusinessException;
import com.fb.exception.entity.CibClientException;
import com.fb.exception.entity.CibClientTechnicalException;
import com.fb.exception.entity.ErrorCode;
import com.fb.exception.entity.ErrorOrigin;

/**
 * Default response mapper to convert response codes to {@link CibClientException}
 */
public abstract class BaseResponseExceptionMapper implements ResponseExceptionMapper {

    protected abstract ErrorOrigin getErrorOrigin();

    /**
     * Converts response status to {@link CibClientBusinessException} if status is 4XX otherwise to
     * {@link CibClientTechnicalException}
     * <p>
     * If the response has an entity it is included as parameter for the error message!
     *
     * @param response to check
     * @return
     */
    @Override
    public Throwable toThrowable(final Response response) {
        final Response.Status status = Response.Status.fromStatusCode(response.getStatus());

        if (!Response.Status.Family.SUCCESSFUL.equals(Response.Status.Family.familyOf(status.getStatusCode()))) {
            final String errorMessage =
                    response.hasEntity() ? response.readEntity(String.class).replaceAll("\"", "") : "is empty.";
            if (Response.Status.Family.CLIENT_ERROR.equals(Response.Status.Family.familyOf(status.getStatusCode()))) {
                return new CibClientBusinessException(ErrorCode.B_EXT_4XX.overrideOrigin(getErrorOrigin()),
                        response.getStatus(), response.getStatusInfo(), errorMessage);
            } else {
                return new CibClientTechnicalException(ErrorCode.T_EXT_5XX.overrideOrigin(getErrorOrigin()),
                        response.getStatus(), response.getStatusInfo(), errorMessage);
            }
        }

        return null;
    }
}
