package com.warehouse.data.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-03-29 09:06
 **/
public class HttpSession implements IHttpSession {

    public static final int BUFFSIZE           = 8192;
    public static final int MEMORY_STORE_LIMIT = 1024;
    public static final int REQUEST_BUFFER_LNE = 512;

    private InputStream  in;
    private OutputStream out;
    private HttpServerTD httpServerTD;

    //header和body分割位置mark
    private int splitbyte;
    //实际读取长度
    private int rlen;

    private Map<String, List<String>> params;
    private Map<String, String>       headers;

    private String queryParameterString;
    private String protocolVersion;
    private String remoteIp;
    private String remoteHostname;
    private String uri;
    private Method method;


    private ITempFileManager tempFileManager;

    public HttpSession(InputStream in, OutputStream out, HttpServerTD httpServerTD, ITempFileManager tempFileManager) {
        this.in = in;
        this.out = out;
        this.httpServerTD = httpServerTD;
        this.tempFileManager = tempFileManager;
    }

    public HttpSession(InputStream in, OutputStream out, HttpServerTD httpServerTD, InetAddress inetAddress, ITempFileManager tempFileManager) {
        this.in = in;
        this.out = out;
        this.httpServerTD = httpServerTD;
        this.remoteIp = inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress() ? "127.0.0.1" : inetAddress.getHostAddress().toString();
        this.remoteHostname = inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress() ? "localhost" : inetAddress.getHostName().toString();

        this.tempFileManager = tempFileManager;
    }


    @Override
    public void execute() throws IOException {
        Response response = null;
        try {
            byte[] buf = new byte[BUFFSIZE];
            this.splitbyte = 0;
            this.rlen = 0;

            int read = -1;
            this.in.mark(BUFFSIZE);


            //apache默认标题为8KB (1024 * 8 = 8192)
            read = in.read(buf, 0, BUFFSIZE);

            if (read == -1) {
                HttpServerTD.safeClose(in);
                HttpServerTD.safeClose(out);
                throw new SocketException("HttpServerTD Shutdown");
            }
            while (read > 0) {
                this.rlen += read;
                this.splitbyte = findHeaderEnd(buf, this.rlen);
                if (this.splitbyte > 0) {
                    break;
                }
                read = this.in.read(buf, this.rlen, BUFFSIZE - this.rlen);
            }

            if (this.splitbyte < this.rlen) {
                this.in.reset();
                this.in.skip(this.splitbyte);
            }

            this.params = new HashMap<>();
            if (null == this.headers) {
                this.headers = new HashMap<>();
            } else {
                this.headers.clear();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf, 0, this.rlen)));
            Map<String, String> pre = new HashMap<>();
            //解析头信息
            decodeHeader(reader, pre, this.params, this.headers);

            if (null != this.remoteIp) {
                this.headers.put("remote-addr", this.remoteIp);
                this.headers.put("http-client-ip", this.remoteIp);
            }

            this.method = Method.lookup(pre.get("method"));
            this.uri = pre.get("uri");
            //this.cookies = null;

            String connection = this.headers.get("connection");
            boolean keepAlive = "HTTP/1.1".equals(protocolVersion) && (connection == null || !connection.matches("(?i).*close*"));

            response = httpServerTD.serve(this);

            if (response == null) {
                throw new HttpServerTD.ResponseException(Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.");
            } else {
                String acceptEncoding = this.headers.get("accept-encoding");
                response.setMethod(this.method);
                response.setEncodeAsGzip(httpServerTD.useGzipWhenAccepted(response) && acceptEncoding != null && acceptEncoding.contains("gzip"));
                response.setKeepAlive(keepAlive);
                response.send(this.out);
            }

            //TODO 判断长链接是否断开

        } catch (SocketException e) {

        } catch (SocketTimeoutException e) {

        } catch (IOException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void parseBody(Map<String, String> files) throws IOException, HttpServerTD.ResponseException {
        RandomAccessFile randomAccessFile = null;

        try {
            long size = getBodySize();
            ByteArrayOutputStream baos = null;
            DataOutput requestDataOutPut = null;

            if (size < MEMORY_STORE_LIMIT) {
                baos = new ByteArrayOutputStream();
                requestDataOutPut = new DataOutputStream(baos);
            } else {
                //file
                randomAccessFile = getTempBucket();
                requestDataOutPut = randomAccessFile;
            }

            //将参数写入DataOutput中
            byte[] buf = new byte[REQUEST_BUFFER_LNE];
            while (this.rlen >= 0 && size > 0) {
                this.rlen = this.in.read(buf, splitbyte, rlen);
                size -= this.rlen;
                if (this.rlen > 0) {
                    requestDataOutPut.write(buf, 0, rlen);
                }
            }

            ByteBuffer fbuf = null;
            if (baos != null) {
                fbuf = ByteBuffer.wrap(baos.toByteArray(), 0, (int) Math.min(size, REQUEST_BUFFER_LNE));
            } else {
                fbuf = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, randomAccessFile.length());
                randomAccessFile.seek(0);
            }

            if (Method.POST.equals(method)) {
                ContentType contentType = new ContentType(this.headers.get("content-length"));
                //文件类型
                if (contentType.isMultipart()) {
                    String boundary = contentType.getBoundary();
                    if (boundary == null) {
                        throw new HttpServerTD.ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html");
                    }

                    decodeMultipartParams(contentType, fbuf, this.params, files);
                } else {
                    byte[] postBytes = new byte[fbuf.remaining()];
                    fbuf.get(postBytes);

                    String postLine = new String(postBytes, contentType.getEncoding()).trim();
                    if ("application/x-www-form-urlencoded".equalsIgnoreCase(contentType.getContentType())) {
                        decodeParams(postLine, this.params);
                    } else {

                    }

                }
            }

        } finally {
            HttpServerTD.safeClose(randomAccessFile);
        }


    }


    private RandomAccessFile getTempBucket() {

        try {
            ITempFile file = this.tempFileManager.createTempFile(null);
            return new RandomAccessFile(file.getName(), "rw");
        } catch (Exception e) {
            throw new Error(e);
        }
    }


    /**
     * 解析多媒体文件
     *
     * @param contentType 内容类型
     * @param fbuf        body内容buffer
     * @param params      参数
     * @param files       文件
     */
    private void decodeMultipartParams(ContentType contentType, ByteBuffer fbuf, Map<String, List<String>> params, Map<String, String> files) {

    }


    /**
     * 获取body实际大小
     *
     * @return size
     */
    private long getBodySize() {
        if (headers.containsKey("content-length")) {
            return Long.parseLong(headers.get("content-length"));
        } else if (this.splitbyte < this.rlen) {
            return this.rlen - this.splitbyte;
        }
        return 0;
    }


    /**
     * 查找头信息结束符位置
     *
     * @param buf  头信息buffer
     * @param rlen 读取头信息实际长度
     * @return 位置
     */
    private int findHeaderEnd(final byte[] buf, int rlen) {
        int splitbyte = 0;
        while (splitbyte + 1 < rlen) {

            if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n' && splitbyte + 3 < rlen && buf[splitbyte + 2] == '\r' && buf[splitbyte + 3] == '\n') {
                return splitbyte + 4;
            }

            if (buf[splitbyte] == '\n' && buf[splitbyte + 1] == '\n') {
                return splitbyte + 2;
            }

            splitbyte++;
        }

        return 0;
    }


    /**
     * 解析头信息
     *
     * @param in      流信息
     * @param pre     存储解析头信息参数
     * @param params  存储解析uri后的实际参数
     * @param headers 头信息
     */
    private void decodeHeader(BufferedReader
                                      in, Map<String, String> pre, Map<String, List<String>> params, Map<String, String> headers) throws
            Exception {
        try {
            String line = in.readLine();
            if (line == null) {
                return;
            }

            StringTokenizer st = new StringTokenizer(line);
            if (!st.hasMoreTokens()) {
                throw new HttpServerTD.ResponseException(Status.BAD_REQUEST, "BAD_REQUEST: Syntax error. Usage: GET /example/file.html");
            }

            pre.put("method", st.nextToken());

            if (!st.hasMoreTokens()) {
                throw new HttpServerTD.ResponseException(Status.BAD_REQUEST, "BAD_REQUEST: Missing URI. Usage: GET /example/file.html");
            }

            String uri = st.nextToken();
            //decode parameters from the URI
            int mark = uri.indexOf('?');
            if (mark > 0) {
                //解析参数
                decodeParams(uri.substring(mark + 1), params);
                uri = HttpServerTD.decodePercent(uri);
            } else {
                uri = HttpServerTD.decodePercent(uri);
            }

            if (st.hasMoreTokens()) {
                this.protocolVersion = st.nextToken();
            } else {
                this.protocolVersion = "HTTP/1.1";
            }

            line = in.readLine();
            while (line != null && !line.trim().isEmpty()) {
                int p = line.indexOf(":");
                if (p >= 0) {
                    headers.put(line.substring(0, p).trim().toUpperCase(Locale.US), line.substring(p + 1).trim());
                }
                line = in.readLine();
            }

            pre.put("uri", uri);

        } catch (IOException e) {
            //e.printStackTrace();
            throw new HttpServerTD.ResponseException(Status.BAD_REQUEST, "SERVER_INTERNAL ERROR: IOException:" + e.getMessage(), e);
        }
    }


    /**
     * 解析uri后的实际参数
     *
     * @param parameters 参数
     * @param params     存储参数集合
     */
    private void decodeParams(String parameters, Map<String, List<String>> params) {
        if (parameters == null) {
            this.queryParameterString = "";
            return;
        }

        this.queryParameterString = parameters;
        StringTokenizer st = new StringTokenizer(parameters, "&");
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf("=");
            String key = null;
            String value = null;

            if (sep > 0) {
                key = HttpServerTD.decodePercent(e.substring(0, sep)).trim();
                value = HttpServerTD.decodePercent(e.substring(sep + 1)).trim();
            } else {
                key = HttpServerTD.decodePercent(e).trim();
                value = "";
            }

            List<String> values = params.get(key);
            if (values == null) {
                values = new ArrayList<>();
                params.put(key, values);
            }

            values.add(value);
        }
    }


    public int getSplitbyte() {
        return splitbyte;
    }

    public void setSplitbyte(int splitbyte) {
        this.splitbyte = splitbyte;
    }

    public int getRlen() {
        return rlen;
    }

    public void setRlen(int rlen) {
        this.rlen = rlen;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> result = new HashMap<>();
        for (String key : this.params.keySet()) {
            result.put(key, this.params.get(key).get(0));
        }

        return result;
    }

    public void setParams(Map<String, List<String>> params) {
        this.params = params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getQueryParameterString() {
        return queryParameterString;
    }

    public void setQueryParameterString(String queryParameterString) {
        this.queryParameterString = queryParameterString;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getRemoteHostname() {
        return remoteHostname;
    }

    public void setRemoteHostname(String remoteHostname) {
        this.remoteHostname = remoteHostname;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
