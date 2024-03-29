package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.exception.DefaultExceptionHandler;
import webserver.exception.ExceptionHandler;
import webserver.request.HttpRequest;
import webserver.request.HttpRequestReader;
import webserver.response.HttpResponse;
import webserver.response.HttpResponseWriter;
import webserver.route.Router;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class WebApplicationServer implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(WebApplicationServer.class);

    private final Socket connection;

    private final List<Class<?>> controllers;
    private final ExceptionHandler exceptionHandler;

    public WebApplicationServer(Socket connectionSocket, List<Class<?>> controllers, ExceptionHandler exceptionHandler) {
        this.connection = connectionSocket;
        this.controllers = controllers;
        this.exceptionHandler = exceptionHandler != null ? exceptionHandler : new DefaultExceptionHandler();
    }

    @Override
    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequestReader httpRequestReader = new HttpRequestReader(in);
            HttpResponseWriter httpResponseWriter = new HttpResponseWriter(out);
            Router router = new Router(controllers);

            runServer(httpRequestReader, httpResponseWriter, router);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void runServer(HttpRequestReader httpRequestReader, HttpResponseWriter httpResponseWriter, Router router) {
        try {
            HttpRequest httpRequest = httpRequestReader.parseInputStream();
            HttpResponse httpResponse = router.route(httpRequest);
            httpResponseWriter.sendResponse(httpResponse);
        } catch (Exception e) {
            httpResponseWriter.sendResponse(exceptionHandler.handleException(e));
        }
    }
}
