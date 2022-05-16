package com.fb.log.control;

import io.quarkiverse.loggingjson.providers.KeyValueStructuredArgument;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.fb.HttpCustomHeaders;
import com.fb.StringUtils;
import com.fb.log.entity.ExclusionLevel;

/**
 * Logging Filter to log incoming and outgoing rest calls
 */
@Provider
@Priority(Integer.MIN_VALUE)
public class LoggingFilter
        implements ContainerRequestFilter, ContainerResponseFilter, ClientRequestFilter, ClientResponseFilter,
        WriterInterceptor, HttpLoggingFilter {

    static final String HTTP_JSON_LOGGER_NAME = "com.bmw.ofcon.logging.HttpJsonLogging";
    private static final String LOGGING_ENTITY = LoggingFilter.class.getName() + ".entity";
    private static final String LOGGING_STATUS = LoggingFilter.class.getName() + ".status";
    private static final String LOGGING_DURATION = LoggingFilter.class.getName() + ".duration";
    private static final String LOG_EXCLUDED = "excluded";
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";

    @Context
    HttpHeaders httpHeaders;

    @ConfigProperty(name = "logging.short")
    Optional<String> shortLoggingLength;

    @ConfigProperty(name = "logging.excluded.all")
    Optional<String> excludedAll;

    @ConfigProperty(name = "logging.excluded.body")
    Optional<String> excludedBody;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        setRequestId();
        checkExclusion(containerRequestContext);
        if (containerRequestContext.getProperty(LOG_EXCLUDED) != ExclusionLevel.ALL) {
            containerRequestContext.setProperty(REQUEST_TIME, System.currentTimeMillis());
            StringBuilder stringBuilder = new StringBuilder();
            printRequestLine(stringBuilder, "Server inbound request", containerRequestContext.getMethod(),
                    containerRequestContext.getUriInfo().getRequestUri());
            printPrefixedHeaders(stringBuilder, PREFIX_REQUEST, containerRequestContext.getHeaders());
            if (containerRequestContext.hasEntity() && MediaType.APPLICATION_JSON.equals(
                    containerRequestContext.getHeaderString(CONTENT_TYPE_HEADER_NAME))) {
                containerRequestContext.setEntityStream(
                        logInboundEntity(stringBuilder, containerRequestContext.getEntityStream(),
                                (ExclusionLevel) containerRequestContext.getProperty(LOG_EXCLUDED)));
            }

            Logger loggerJson = getInterceptorLogger();
            if (loggerJson.isInfoEnabled()) {
                loggerJson.info(stringBuilder.toString());
            }
        }
    }

    Logger getInterceptorLogger() {
        return LoggerFactory.getLogger(HTTP_JSON_LOGGER_NAME);
    }

    private void checkExclusion(ContainerRequestContext containerRequestContext) {
        containerRequestContext.setProperty(LOG_EXCLUDED, ExclusionLevel.NONE);
        excludedBody.ifPresent(s -> setExclusionIfPresent(containerRequestContext, s, ExclusionLevel.BODY));
        excludedAll.ifPresent(s -> setExclusionIfPresent(containerRequestContext, s, ExclusionLevel.ALL));
    }

    private void checkExclusion(final ClientRequestContext clientRequestContext) { //NOSONAR: 'Unused "private"
        // methods should be removed' but is used
        clientRequestContext.setProperty(LOG_EXCLUDED, ExclusionLevel.NONE);
        excludedBody.ifPresent(s -> setExclusionIfPresent(clientRequestContext, s, ExclusionLevel.BODY));
        excludedAll.ifPresent(s -> setExclusionIfPresent(clientRequestContext, s, ExclusionLevel.ALL));
    }

    private void setExclusionIfPresent(ContainerRequestContext containerRequestContext, String config,
            ExclusionLevel exclusionLevel) {
        List<String> excludedServices = Arrays.asList(config.split(","));
        Optional<String> excludedService = excludedServices.stream().filter(s -> {
            String[] service = s.trim().split(" ");
            return service.length == 2 && containerRequestContext.getMethod().equals(service[0]) &&
                    containerRequestContext.getUriInfo().getRequestUri().toASCIIString().matches(service[1]);
        }).findFirst();
        if (excludedService.isPresent()) {
            containerRequestContext.setProperty(LOG_EXCLUDED, exclusionLevel);
        }
    }

    private void setExclusionIfPresent(final ClientRequestContext clientRequestContext, String config, //NOSONAR:
            // 'Unused "private" methods should be removed' but is used
            final ExclusionLevel exclusionLevel) {
        List<String> excludedServices = Arrays.asList(config.split(","));
        Optional<String> excludedService = excludedServices.stream().filter(s -> {
            String[] service = s.trim().split(" ");
            return service.length == 2 && clientRequestContext.getMethod().equals(service[0]) &&
                    clientRequestContext.getUri().toASCIIString().matches(service[1]);
        }).findFirst();
        if (excludedService.isPresent()) {
            clientRequestContext.setProperty(LOG_EXCLUDED, exclusionLevel);
        }
    }

    private void setRequestId() {
        List<String> requestIdList = httpHeaders.getRequestHeader(HttpCustomHeaders.REQUEST_ID);
        String requestId = null;
        if (requestIdList != null && !requestIdList.isEmpty()) {
            requestId = requestIdList.get(0);
        }
        if (StringUtils.isBlank(requestId)) {
            requestId = generateRequestId();
        }
        MDC.put(HttpCustomHeaders.REQUEST_ID, requestId);
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext,
            ContainerResponseContext containerResponseContext) {
        if (containerRequestContext.getProperty(LOG_EXCLUDED) != ExclusionLevel.ALL) {
            StringBuilder stringBuilder = new StringBuilder();
            containerResponseContext.getHeaders()
                    .add(HttpCustomHeaders.REQUEST_ID, MDC.get(HttpCustomHeaders.REQUEST_ID));
            printResponseLine(stringBuilder, "Server outbound response", containerResponseContext.getStatus());
            printPrefixedHeaders(stringBuilder, PREFIX_RESPONSE, containerResponseContext.getStringHeaders());
            final Map<String, String> httpJsonFields = createHttpJsonMap(containerRequestContext.getMethod(),
                    containerRequestContext.getUriInfo().getRequestUri().toASCIIString(),
                    containerResponseContext.getStatus(), containerRequestContext.getProperty(REQUEST_TIME));
            if (containerResponseContext.hasEntity()) {
                OutputStream outputStream =
                        new LoggingStream(stringBuilder, containerResponseContext.getEntityStream());
                containerResponseContext.setEntityStream(outputStream);
                containerRequestContext.setProperty(LOGGING_ENTITY, outputStream);
                containerRequestContext.setProperty(LOGGING_STATUS,
                        String.valueOf(containerResponseContext.getStatus()));
                containerRequestContext.setProperty(LOGGING_DURATION, getDuration(containerRequestContext.getMethod(),
                        containerRequestContext.getUriInfo().getRequestUri().toASCIIString(),
                        containerResponseContext.getStatus(), containerRequestContext.getProperty(REQUEST_TIME)));
            } else {
                Logger loggerJson = getInterceptorLogger();
                if (loggerJson.isInfoEnabled()) {
                    loggerJson.info("#### SERVICE_EXIT: " + stringBuilder, httpJsonFields.entrySet().stream()
                            .map(e -> KeyValueStructuredArgument.kv(e.getKey(), e.getValue())).toArray());
                }
            }
        }
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext)
            throws IOException, WebApplicationException {
        LoggingStream loggingStream = (LoggingStream) writerInterceptorContext.getProperty(LOGGING_ENTITY);
        String status = (String) writerInterceptorContext.getProperty(LOGGING_STATUS);
        String durationMessage = (String) writerInterceptorContext.getProperty(LOGGING_DURATION);
        ExclusionLevel exclusionLevel = (ExclusionLevel) writerInterceptorContext.getProperty(LOG_EXCLUDED);
        writerInterceptorContext.proceed();
        if (loggingStream != null) {
            addEntityToStringBuilder(loggingStream.getStringBuilder(), loggingStream.getEntity(), status,
                    exclusionLevel);

            Logger loggerJson = getInterceptorLogger();
            if (loggerJson.isInfoEnabled()) {
                loggerJson.info("#### SERVICE_EXIT: " + loggingStream.getStringBuilder().toString());
                loggerJson.info(durationMessage);
            }
        }
    }

    @Override
    public Integer getShortLoggingLength() {
        if (shortLoggingLength.isPresent()) {
            Integer result = StringUtils.parseToInt(shortLoggingLength.get());
            return result != null ? result : Integer.MAX_VALUE;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public void filter(final ClientRequestContext clientRequestContext) {
        if (StringUtils.isBlank(MDC.get(HttpCustomHeaders.REQUEST_ID))) {
            MDC.put(HttpCustomHeaders.REQUEST_ID, generateRequestId());
        }
        checkExclusion(clientRequestContext);
        if (clientRequestContext.getProperty(LOG_EXCLUDED) != ExclusionLevel.ALL) {
            clientRequestContext.setProperty(REQUEST_TIME, System.currentTimeMillis());
            clientRequestContext.getHeaders().put(HttpCustomHeaders.REQUEST_ID,
                    Collections.singletonList(MDC.get(HttpCustomHeaders.REQUEST_ID)));

            StringBuilder stringBuilder = new StringBuilder();

            printRequestLine(stringBuilder, "Sending client request", clientRequestContext.getMethod(),
                    clientRequestContext.getUri());
            printPrefixedHeaders(stringBuilder, PREFIX_REQUEST, clientRequestContext.getStringHeaders());

            if (clientRequestContext.hasEntity()) {
                OutputStream stream = new LoggingStream(stringBuilder, clientRequestContext.getEntityStream());
                clientRequestContext.setEntityStream(stream);
                clientRequestContext.setProperty(LOGGING_ENTITY, stream);
            } else {
                Logger loggerJson = getInterceptorLogger();
                if (loggerJson.isInfoEnabled()) {
                    loggerJson.info(stringBuilder.toString());
                }
            }
        }
    }

    @Override
    public void filter(final ClientRequestContext clientRequestContext,
            final ClientResponseContext clientResponseContext) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        printResponseLine(stringBuilder, "Client response received", clientResponseContext.getStatus());
        printPrefixedHeaders(stringBuilder, PREFIX_RESPONSE, clientResponseContext.getHeaders());

        if (clientResponseContext.hasEntity() &&
                isPrintableContentType(clientResponseContext.getHeaderString(HttpHeaders.CONTENT_TYPE))) {
            clientResponseContext.setEntityStream(
                    logInboundEntity(stringBuilder, clientResponseContext.getEntityStream(),
                            (ExclusionLevel) clientRequestContext.getProperty(LOG_EXCLUDED)));
        }
        final Map<String, String> httpJsonFields =
                createHttpJsonMap(clientRequestContext.getMethod(), clientRequestContext.getUri().toASCIIString(),
                        clientResponseContext.getStatus(), clientRequestContext.getProperty(REQUEST_TIME));

        Logger loggerJson = getInterceptorLogger();
        if (loggerJson.isInfoEnabled()) {
            loggerJson.info(stringBuilder.toString(),
                    httpJsonFields.entrySet().stream().map(e -> KeyValueStructuredArgument.kv(e.getKey(), e.getValue()))
                            .toArray());
        }
    }
}
