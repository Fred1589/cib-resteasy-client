package com.fb.exception.control;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.fb.exception.boundary.ExceptionHandlerInterceptor;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marker interface for exception handler interceptor.
 * <p>
 * Annotate REST resources with this interface to enable central exception handling!
 *
 * @see ExceptionHandlerInterceptor
 */
@Inherited
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@InterceptorBinding
public @interface ExceptionHandler {
}
