package com.fb.log.control;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Stream that will be logged
 */
public class LoggingStream extends FilterOutputStream {

    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private final StringBuilder stringBuilder;

    /**
     * Creates an output stream filter built on top of the specified
     * underlying output stream.
     *
     * @param stringBuilder containing log string
     * @param out           the underlying output stream to be assigned to
     *                      the field {@code this.out} for later use, or
     *                      {@code null} if this instance is to be
     *                      created without an underlying stream.
     */
    public LoggingStream(StringBuilder stringBuilder, OutputStream out) {
        super(out);
        this.stringBuilder = stringBuilder;
    }

    @Override
    public void write(int i) throws IOException {
        this.byteArrayOutputStream.write(i);
        this.out.write(i);
    }

    public byte[] getEntity() {
        return this.byteArrayOutputStream.toByteArray();
    }

    public StringBuilder getStringBuilder() {
        return this.stringBuilder;
    }
}
