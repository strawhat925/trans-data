package com.warehouse.data.http;


import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-03-29 12:37
 **/
public class Response implements Closeable {


    private final Map<String, String> lowerCaseHeaders = new HashMap<String, String>();
    private final Map<String, String> headers          = new HashMap<String, String>() {
        @Override
        public String put(String key, String value) {
            lowerCaseHeaders.put(key != null ? key.toLowerCase() : key, value);
            return super.put(key, value);
        }
    };

    private String       mimeTypes;
    private InputStream  data;
    private long         contentLength;
    private Method       method;
    private boolean      chunkedTransfer;
    private boolean      encodeAsGzip;
    private boolean      keepAlive;
    private List<String> cookieHeaders;
    private IStatus      status;


    public Response(IStatus status, String mimeType, ByteArrayInputStream data, int length) {
        this.status = status;
        this.mimeTypes = mimeType;
        if (data == null) {
            this.data = new ByteArrayInputStream(new byte[0]);
            this.contentLength = 0L;
        } else {
            this.data = data;
            this.contentLength = length;
        }
        this.chunkedTransfer = this.contentLength < 0;
        this.keepAlive = true;
        this.cookieHeaders = new ArrayList<>(10);
    }

    @Override
    public void close() throws IOException {
        if (data != null) {
            data.close();
        }
    }


    public String getMimeTypes() {
        return mimeTypes;
    }

    public void setMimeTypes(String mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public InputStream getData() {
        return data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean isChunkedTransfer() {
        return chunkedTransfer;
    }

    public void setChunkedTransfer(boolean chunkedTransfer) {
        this.chunkedTransfer = chunkedTransfer;
    }

    public boolean isEncodeAsGzip() {
        return encodeAsGzip;
    }

    public void setEncodeAsGzip(boolean encodeAsGzip) {
        this.encodeAsGzip = encodeAsGzip;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public List<String> getCookieHeaders() {
        return cookieHeaders;
    }

    public void setCookieHeaders(List<String> cookieHeaders) {
        this.cookieHeaders = cookieHeaders;
    }


    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }


    public String getHeader(String name) {
        return this.lowerCaseHeaders.get(name.toLowerCase());
    }

    public static Response newFixedLengthResponse(IStatus status, String mimeType, String txt) {
        ContentType contentType = new ContentType(mimeType);

        if (txt == null) {
            return newFixedLengthResponse(status, contentType.getContentTypeHeader(), new ByteArrayInputStream(new byte[0]), 0);
        } else {
            byte[] bytes;
            try {
                CharsetEncoder newEncoder = Charset.forName(contentType.getEncoding()).newEncoder();
                if (!newEncoder.canEncode(txt)) {
                    contentType = contentType.tryUTF8();
                }
                bytes = txt.getBytes(contentType.getEncoding());
            } catch (UnsupportedEncodingException e) {
                //e.printStackTrace();
                bytes = new byte[0];
            }

            return newFixedLengthResponse(status, contentType.getContentTypeHeader(), new ByteArrayInputStream(bytes), bytes.length);
        }

    }

    private static Response newFixedLengthResponse(IStatus status, String mimeType, ByteArrayInputStream byteArrayInputStream, int length) {
        return new Response(status, mimeType, byteArrayInputStream, length);
    }


    public boolean isConnectionClose() {
        return "close".equals(getHeader("connection"));
    }


    public void send(OutputStream out) {
        SimpleDateFormat gmtFrmt = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            if (status == null) {
                throw new Error("sendResponse(): Status can't be null.");
            }
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, new ContentType(mimeTypes).getEncoding())), false);
            pw.append("HTTP/1.1 ").append(status.getDescription()).append("\r\n");

            if (mimeTypes != null) {
                printHeader(pw, "Content-Type", mimeTypes);
            }

            if (getHeader("date") != null) {
                printHeader(pw, "Date", gmtFrmt.format(new Date()));
            }

            headers.forEach((k, v) -> {
                printHeader(pw, k, v);
            });

            cookieHeaders.forEach(action -> {
                printHeader(pw, "Set-Cookie", action);
            });

            if (getHeader("connection") == null) {
                printHeader(pw, "Connection", this.keepAlive ? "keep-alive" : "close");
            }

            if (getHeader("content-length") != null) {
                encodeAsGzip = false;
            }

            if (encodeAsGzip) {
                printHeader(pw, "Content-Encoding", "gzip");
                setChunkedTransfer(true);
            }

            long pending = this.data != null ? this.contentLength : 0;
            if (!this.method.equals(Method.HEAD) && this.chunkedTransfer) {
                printHeader(pw, "Transfer-Encoding", "chunked");
            } else if (!encodeAsGzip) {
                pending = sendContentLengthHeaderIfNotAlreadyPresent(pw, pending);
            }
            pw.append("\r\n");
            pw.flush();

            sendBodyWithCorrectTransferAndEncoding(out, pending);
            out.flush();
            HttpServerTD.safeClose(data);
        } catch (IOException e) {
            System.out.println(Level.SEVERE + " Could not send response to the client");
        }
    }


    protected long sendContentLengthHeaderIfNotAlreadyPresent(PrintWriter pw, long defaultSize) {
        String contentLengthString = getHeader("content-length");
        long size = defaultSize;
        if (contentLengthString != null) {
            try {
                size = Long.parseLong(contentLengthString);
            } catch (NumberFormatException ex) {
                System.out.println("content-length was no number " + contentLengthString);
            }
        }
        pw.print("Content-Length: " + size + "\r\n");
        return size;
    }

    private void sendBodyWithCorrectTransferAndEncoding(OutputStream outputStream, long pending) throws IOException {
        if (this.method != Method.HEAD && this.chunkedTransfer) {
            ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(outputStream);
            sendBodyWithCorrectEncoding(chunkedOutputStream, -1);
            chunkedOutputStream.finish();
        } else {
            sendBodyWithCorrectEncoding(outputStream, pending);
        }
    }


    private void sendBodyWithCorrectEncoding(OutputStream outputStream, long pending) throws IOException {
        if (encodeAsGzip) {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            sendBody(gzipOutputStream, -1);
            gzipOutputStream.finish();
        } else {
            sendBody(outputStream, pending);
        }
    }


    private void sendBody(OutputStream outputStream, long pending) throws IOException {
        long BUFFER_SIZE = 16 * 1024;
        byte[] buff = new byte[(int) BUFFER_SIZE];
        boolean sendEverything = pending == -1;
        while (pending > 0 || sendEverything) {
            long bytesToRead = sendEverything ? BUFFER_SIZE : Math.min(pending, BUFFER_SIZE);
            int read = this.data.read(buff, 0, (int) bytesToRead);
            if (read <= 0) {
                break;
            }
            outputStream.write(buff, 0, read);
            if (!sendEverything) {
                pending -= read;
            }
        }
    }

    private void printHeader(PrintWriter pw, String key, String value) {
        pw.append(key).append(": ").append(value).append("\r\n");
    }

}
