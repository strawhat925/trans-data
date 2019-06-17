package com.warehouse.data.http;


import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-03-27 14:06
 **/
public class HttpServerTD {


    public final String       host;
    public final int          port;
    private      ServerSocket serverSocket;
    private      Thread       thread;
    public       IAsynRunner  iAsynRunner;
    public static final String QUERY_STRING_PARAMETER = "HSD_STRING";
    public static final String MIME_TEXTPLAIN         = "text/plain";



    public static void main(String[] args) {
        try {
            new HttpServerTD(null, 8080).start(0, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public HttpServerTD(String host, int port) {
        this.host = host;
        this.port = port;


        //
        setTempFileManagerFactory(new DefaultTempFileManagerFactory());
        //
        setAysnRunner(new DefaultAsynRunner(200));
    }

    private void setAysnRunner(IAsynRunner iAsynRunner) {
        this.iAsynRunner = iAsynRunner;
    }


    private DefaultSocketFactory socketFactory = new DefaultSocketFactory();

    private IFactory<DefaultTempFileManager> tempFileManagerFactory;

    private ServerRunnable createServerRunnable(int timeout) {
        return new ServerRunnable(this, timeout);
    }

    public ClientHandler createHandler(final InputStream inputStream, final Socket socket) {
        return new ClientHandler(inputStream, socket, this);
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void start(final int timeout, final boolean daemon) throws IOException {
        //create ServerSocket server
        serverSocket = socketFactory.create();
        serverSocket.setReuseAddress(true);

        ServerRunnable serverRunnable = createServerRunnable(timeout);
        thread = new Thread(serverRunnable);
        thread.setDaemon(daemon);
        thread.setName("HttpServerTD Main Thread");
        thread.start();


        //System.out.println("server start .");
        //保证绑定端口成功
        while (serverRunnable.isHasBind() && serverRunnable.getBindException() != null) {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //throw ex
        if (serverRunnable.getBindException() != null) {
            throw serverRunnable.getBindException();
        }

    }

    public static final void safeClose(Object closeable) {
        try {
            if (closeable != null) {
                if (closeable instanceof Closeable) {
                    ((Closeable) closeable).close();
                } else if (closeable instanceof Socket) {
                    ((Socket) closeable).close();
                } else if (closeable instanceof ServerSocket) {
                    ((ServerSocket) closeable).close();
                } else {
                    throw new IllegalArgumentException("Unknown object to close");
                }
            }
        } catch (IOException e) {
            HttpServerTD.print("Could not close", e);
        }
    }

    public void close() {
        try {
            safeClose(serverSocket);
            if (thread != null) {
                thread.join();
            }
        } catch (Exception e) {
            HttpServerTD.print("Could not stop all connections", e);
        }
    }


    public static class DefaultSocketFactory {

        public ServerSocket create() throws IOException {
            return new ServerSocket();
        }

    }


    public static void print(String str, Exception... e) {
        System.out.println(str);

        if (e != null && e.length > 0) {
            e[0].printStackTrace();
        }
    }


    public static String decodePercent(String str) {
        String decoded = null;

        try {
            decoded = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return decoded;
    }


    public Response serve(HttpSession httpSession) {
        Map<String, String> files = new HashMap<>();
        Method method = httpSession.getMethod();
        //POST解析body
        if (Method.POST.equals(method)) {
            try {
                httpSession.parseBody(files);
            } catch (IOException e) {
                //e.printStackTrace();
                return Response.newFixedLengthResponse(Status.INTERNAL_ERROR, MIME_TEXTPLAIN, "SERVER_INTERNAL_ERROR: IOException:" + e.getMessage());
            } catch (ResponseException e) {
                //e.printStackTrace();
                return Response.newFixedLengthResponse(e.getStatus(), MIME_TEXTPLAIN, e.getMessage());
            }
        }

        Map<String, String> params = httpSession.getParams();
        params.put(QUERY_STRING_PARAMETER, httpSession.getQueryParameterString());
        return serve(httpSession.getUri(), method, httpSession.getHeaders(), params, files);
    }

    /**
     * 继承该类重写serve方法，处理具体的uri
     */
    @Deprecated
    private Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
        return Response.newFixedLengthResponse(Status.NOT_FOUND, MIME_TEXTPLAIN, "NOT FOUNT");
    }


    public boolean useGzipWhenAccepted(Response response){
        return response.getMimeTypes() != null && (response.getMimeTypes().toLowerCase().contains("text/") || response.getMimeTypes().toLowerCase().contains("json/"));
    }


    public IFactory<DefaultTempFileManager> getTempFileManagerFactory() {
        return tempFileManagerFactory;
    }

    public void setTempFileManagerFactory(IFactory<DefaultTempFileManager> tempFileManagerFactory) {
        this.tempFileManagerFactory = tempFileManagerFactory;
    }

    public final static class ResponseException extends Exception {

        private Status status;

        public ResponseException(Status status, String message) {
            super(message);
            this.status = status;
        }

        public ResponseException(Status status, String message, Exception e) {
            super(message, e);
            this.status = status;
        }

        public Status getStatus() {
            return status;
        }

    }
}
