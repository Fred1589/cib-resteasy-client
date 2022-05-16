package com.fb.exception.boundary;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fb.exception.control.ExceptionHandler;
import com.fb.exception.control.ExceptionUtil;
import com.fb.exception.entity.CibClientBusinessException;
import com.fb.exception.entity.CibClientException;
import com.fb.exception.entity.CibClientTechnicalException;
import com.fb.exception.entity.ErrorCode;

/**
 * Around invoke interceptor to catch all exceptions thrown from application methods to allow central handling.
 */
@Priority(Interceptor.Priority.APPLICATION)
@ExceptionHandler
@Interceptor
public class ExceptionHandlerInterceptor {

    private static final String METHOD_AND_ARGS = "\\w+\\.";

    /**
     * Around invoke interceptor implementation to catch exceptions on method invocation.
     * Non {@link CibClientException} are rethrown as WebApplicationExceptions
     *
     * @param ctx the invocation context when intercepting a method call
     * @return the result of the normal method invocation of intercepted call
     * @throws WebApplicationException the intercepted exception is rethrown after catch processing
     */
    @AroundInvoke
    public Object handleExceptionOnInvocation(final InvocationContext ctx) {
        try {
            return ctx.proceed();
        } catch (final Exception e) {
            if (ExceptionUtil.causalChainContainsClass(e, CibClientException.class)) {
                final CibClientException cibClientException =
                        ExceptionUtil.extractException(e, CibClientException.class);

                final Logger logger = LoggerFactory.getLogger(ctx.getTarget().getClass());
                logger.error(e.getMessage(), e);

                throw cibClientException;
            } else if (ExceptionUtil.causalChainContainsClass(e, ValidationException.class)) {
                final ValidationException validationException =
                        ExceptionUtil.extractException(e, ValidationException.class);
                final CibClientBusinessException businessException =
                        new CibClientBusinessException(ErrorCode.B_INVALID_PARAMETER,
                                validationException.getMessage().replaceAll(METHOD_AND_ARGS, ""));

                final Logger logger = LoggerFactory.getLogger(ctx.getTarget().getClass());
                logger.error(e.getMessage(), e);

                throw businessException;
            } else if (ExceptionUtil.causalChainContainsClass(e, ConstraintViolationException.class)) {
                final ConstraintViolationException validationException =
                        ExceptionUtil.extractException(e, ConstraintViolationException.class);
                final CibClientBusinessException businessException =
                        new CibClientBusinessException(ErrorCode.B_INVALID_PARAMETER,
                                validationException.getMessage().replaceAll(METHOD_AND_ARGS, ""));

                final Logger logger = LoggerFactory.getLogger(ctx.getTarget().getClass());
                logger.error(e.getMessage(), e);

                throw businessException;
            } else {
                final Logger logger = LoggerFactory.getLogger(ctx.getTarget().getClass());
                logger.error("An unexpected exception occurred", e);

                throw new CibClientTechnicalException(ErrorCode.T_INTERNAL_SERVER_ERROR);
            }
        }
    }
}
