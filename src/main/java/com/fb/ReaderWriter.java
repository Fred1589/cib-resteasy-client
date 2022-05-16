package com.fb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Read Writer to write content from input stream to output stream
 */
public final class ReaderWriter {

    private ReaderWriter() {
        // private constructor to hide the public one
    }

    /**
     * Write input stream to output stream
     *
     * @param in  input stream
     * @param out output streamm
     * @throws IOException thrown when error occurs on stream
     */
    public static void writeTo(InputStream in, OutputStream out) throws IOException {
        byte[] data = new byte[8192];

        int read;
        while ((read = in.read(data)) != -1) {
            out.write(data, 0, read);
        }

    }
}
