package com.fb.log.control;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fb.ReaderWriter;
import com.fb.StringUtils;
import com.fb.log.entity.ExclusionLevel;

/**
 * Functional Interface for HTTP Logging
 */
public interface HttpLoggingFilter {


    SecureRandom random = new SecureRandom();
    int NUMBER_OF_BIG_INTEGER_BITS = 32;

    String PREFIX_NOTIFICATION = " * ";
    String PREFIX_REQUEST = "REQ > ";
    String PREFIX_RESPONSE = "RESP < ";
    String REQUEST_TIME = "requestTime";

    List<String> authHeaders =
            Arrays.asList(HttpHeaders.AUTHORIZATION.toLowerCase(), "x-api-key", "keyid", "x-apikey", "apikey");
    List<String> unprintableContentTypes =
            Arrays.asList("image/png", "image/jpeg", "image/jpg", MediaType.APPLICATION_OCTET_STREAM,
                    "application/pdf");
    /* json logging field name */ String JSON_METHOD = "method";
    String JSON_URI = "uri";
    String JSON_STATUS = "status";
    String JSON_DURATION = "duration";

    /**
     * Appends the given entity to the log string.
     * Also a short logging mechanism is given on http call level (method, uri, status)
     *
     * @param stringBuilder  string builder with log string
     * @param entity         byte array to log
     * @param status         status to check if short logging is necessary
     * @param exclusionLevel describes if body is logged
     */
    default void addEntityToStringBuilder(StringBuilder stringBuilder, byte[] entity, String status,
            ExclusionLevel exclusionLevel) {
        if (entity == null || entity.length == 0 || ExclusionLevel.BODY == exclusionLevel) {
            return;
        }
        String s = new String(entity, StandardCharsets.UTF_8).trim();
        Integer shortLoggingLength = getShortLoggingLength();
        if ((status == null || status.startsWith("2")) && shortLoggingLength < Integer.MAX_VALUE &&
                s.length() > shortLoggingLength) {
            s = s.substring(0, shortLoggingLength) + "(...)";
        }
        stringBuilder.append(s).append(" # ");
    }

    /**
     * Append request to log string
     *
     * @param stringBuilder string builder with log string
     * @param note          note
     * @param method        method
     * @param uri           request uri
     */
    default void printRequestLine(StringBuilder stringBuilder, String note, String method, URI uri) {
        stringBuilder.append(PREFIX_NOTIFICATION).append(note).append(" on thread ")
                .append(Thread.currentThread().getName()).append("\n");
        stringBuilder.append(PREFIX_REQUEST).append(method).append(" ").append(uri.toASCIIString()).append("\n");
    }

    /**
     * Append response to log string
     *
     * @param stringBuilder string builder with log string
     * @param note          note
     * @param status        http status
     */
    default void printResponseLine(StringBuilder stringBuilder, String note, int status) {
        stringBuilder.append(PREFIX_NOTIFICATION).append(note).append(" on thread ")
                .append(Thread.currentThread().getName()).append("\n");
        stringBuilder.append(PREFIX_RESPONSE).append(status).append("\n");
    }

    /**
     * Append headers to log string
     *
     * @param stringBuilder string builder with log string
     * @param prefix        prefix for headers
     * @param headers       multivalued map with headers
     */
    default void printPrefixedHeaders(StringBuilder stringBuilder, String prefix,
            MultivaluedMap<String, String> headers) {
        headers.forEach((k, v) -> {
            StringBuilder sb = new StringBuilder();
            if (authHeaders.contains(k.toLowerCase())) {
                sb.append("***");
            } else {
                v.forEach(s -> sb.append(s).append(","));
                sb.deleteCharAt(sb.lastIndexOf(","));
            }
            stringBuilder.append(prefix).append(k).append(": ").append(sb).append(" # ");
        });
        stringBuilder.append("\n");
    }

    /**
     * Append incoming request body to log string
     *
     * @param stringBuilder  string builder with log string
     * @param stream         incoming stream
     * @param exclusionLevel describes if body is logged
     * @return reset input stream
     * @throws IOException thrown in error case on input stream
     */
    default InputStream logInboundEntity(StringBuilder stringBuilder, InputStream stream, ExclusionLevel exclusionLevel)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ReaderWriter.writeTo(stream, out);
        byte[] requestEntity = out.toByteArray();
        this.addEntityToStringBuilder(stringBuilder, requestEntity, null, exclusionLevel);
        return new ByteArrayInputStream(requestEntity);
    }

    /**
     * Returns log string for duration of request
     *
     * @param method   http method
     * @param uri      request uri
     * @param status   http status
     * @param duration duration of call
     * @return 'method' 'uri' answered with status 'status' and took 'duration' ms
     */
    default String getDuration(String method, String uri, int status, Object duration) {
        return duration != null ? String.format("%s %s answered with status %s and took %s ms", method, uri, status,
                System.currentTimeMillis() - (long) duration) :
                String.format("%s %s answered with status %s", method, uri, status);
    }

    /**
     * Creates the http Json map fields for http logging.
     *
     * @param method   http method
     * @param uri      request uri
     * @param status   http status
     * @param duration duration of call
     * @return map of the different Json parameters
     */
    default Map<String, String> createHttpJsonMap(String method, String uri, int status, Object duration) {
        Map<String, String> httpJsonFields = new HashMap<>();
        if (duration != null) {
            final long totalDuration = System.currentTimeMillis() - (long) duration;
            httpJsonFields.put(JSON_DURATION, String.valueOf(totalDuration));
        }
        httpJsonFields.put(JSON_URI, uri);
        httpJsonFields.put(JSON_METHOD, method);
        httpJsonFields.put(JSON_STATUS, String.valueOf(status));
        return httpJsonFields;
    }

    default boolean isPrintableContentType(String contentType) {
        return !StringUtils.isBlank(contentType) && !unprintableContentTypes.contains(contentType);
    }

    /**
     * Generate a new request id
     *
     * @return generated request id
     */
    default String generateRequestId() {
        return new BigInteger(NUMBER_OF_BIG_INTEGER_BITS, random).toString(Character.MAX_RADIX);
    }

    default Integer getShortLoggingLength() {
        return Integer.MAX_VALUE;
    }
}
