package android;/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;
import javax.xml.bind.DatatypeConverter;

/**
 * Utility class for managing input streams.
 */
public class StreamUtil {
    // 16K buffer size
    private static final int BUF_SIZE = 16 * 1024;

    private StreamUtil() {
    }

    /**
     * Retrieves a {@link String} from a character stream.
     *
     * @param stream the {@link InputStream}
     * @return a {@link String} containing the stream contents
     * @throws IOException if failure occurred reading the stream
     */
    public static String getStringFromStream(InputStream stream) throws IOException {
        Reader ir = new BufferedReader(new InputStreamReader(stream));
        int irChar = -1;
        StringBuilder builder = new StringBuilder();
        while ((irChar = ir.read()) != -1) {
            builder.append((char) irChar);
        }
        return builder.toString();
    }

    /**
     * Copies contents of origStream to destStream.
     * <p/>
     * Recommended to provide a buffered stream for input and output
     *
     * @param inStream  the {@link InputStream}
     * @param outStream the {@link OutputStream}
     * @throws IOException
     */
    public static void copyStreams(InputStream inStream, OutputStream outStream)
            throws IOException {
        byte[] buf = new byte[BUF_SIZE];
        int size = -1;
        while ((size = inStream.read(buf)) != -1) {
            outStream.write(buf, 0, size);
        }
    }

    /**
     * Copies contents of inStream to writer.
     * <p/>
     * Recommended to provide a buffered stream for input and output
     *
     * @param inStream the {@link InputStream}
     * @param writer   the {@link Writer} destination
     * @throws IOException
     */
    public static void copyStreamToWriter(InputStream inStream, Writer writer) throws IOException {
        byte[] buf = new byte[BUF_SIZE];
        int size = -1;
        while ((size = inStream.read(buf)) != -1) {
            writer.write(new String(buf, 0, size));
        }
    }

    /**
     * Gets the stack trace as a {@link String}.
     *
     * @param throwable the {@link Throwable} to convert.
     * @return a {@link String} stack trace
     */
    public static String getStackTrace(Throwable throwable) {
        // dump the print stream results to the ByteArrayOutputStream, so contents can be evaluated
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream bytePrintStream = new PrintStream(outputStream);
        throwable.printStackTrace(bytePrintStream);
        return outputStream.toString();
    }

    /**
     * @deprecated use {@link #close(Closeable)} instead.
     */
    @Deprecated
    public static void closeStream(OutputStream out) {
        close(out);
    }

    /**
     * @deprecated use {@link #close(Closeable)} instead.
     */
    @Deprecated
    public static void closeStream(InputStream in) {
        close(in);
    }

    /**
     * Attempts to flush the given output stream, and then closes it.
     *
     * @param outStream the {@link OutputStream}. No action taken if outStream is null.
     */
    public static void flushAndCloseStream(OutputStream outStream) {
        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                // ignore
            }
            try {
                outStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Closes given zip output stream.
     *
     * @param outStream the {@link ZipOutputStream}. No action taken if outStream is null.
     */
    public static void closeZipStream(ZipOutputStream outStream) {
        if (outStream != null) {
            try {
                outStream.closeEntry();
                outStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Closes given gzip output stream.
     *
     * @param outStream the {@link ZipOutputStream}. No action taken if outStream is null.
     */
    public static void closeGZipStream(GZIPOutputStream outStream) {
        if (outStream != null) {
            try {
                outStream.finish();
                outStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Closes the given {@link Closeable}.
     *
     * @param closeable the {@link Closeable}. No action taken if <code>null</code>.
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Create a {@link OutputStream} that discards all writes.
     * <p>
     * TODO: replace with guava's ByteStreams.nullOutputStream() when guava is updated.
     */
    public static OutputStream nullOutputStream() {
        return new OutputStream() {
            /** Discards the specified byte. */
            @Override
            public void write(int b) {
            }

            /** Discards the specified byte array. */
            @Override
            public void write(byte[] b, int off, int len) {
            }
        };
    }

    /**
     * Helper method to calculate md5 for a inputStream. The inputStream will be consumed and
     * closed.
     *
     * @param inputSource used to create inputStream
     * @return md5 of the stream
     * @throws IOException
     */
    public static String calculateMd5(InputStream inputSource) throws IOException {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            // This should not happen
            throw new RuntimeException(e);
        }
        InputStream input = new BufferedInputStream(new DigestInputStream(inputSource, md));
        byte[] buf = new byte[BUF_SIZE];
        while (input.read(buf) != -1) {
            // Read through the stream to update digest.
        }
        input.close();
        String md5 = DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
        return md5;
    }
}