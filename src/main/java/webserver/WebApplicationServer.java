package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.exception.DefaultExceptionHandler;
import webserver.exception.ExceptionHandler;
import webserver.exception.server.UnauthorizedException;
import webserver.filter.Filter;
import webserver.filter.Filtering;
import webserver.http.type.HttpRequest;
import webserver.http.HttpRequestReader;
import webserver.http.type.HttpResponse;
import webserver.http.HttpResponseWriter;
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
    private final List<Filter> filters;
    private final ExceptionHandler exceptionHandler;

    public WebApplicationServer(Socket connectionSocket, List<Class<?>> controllers, List<Filter> filters, ExceptionHandler exceptionHandler) {
        this.connection = connectionSocket;
        this.controllers = controllers;
        this.filters = filters;
        this.exceptionHandler = exceptionHandler != null ? exceptionHandler : new DefaultExceptionHandler();
    }

    @Override
    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequestReader httpRequestReader = new HttpRequestReader(in);
            HttpResponseWriter httpResponseWriter = new HttpResponseWriter(out);
            Filtering filtering = new Filtering(filters);
            Router router = new Router(controllers);

            runServer(httpRequestReader, httpResponseWriter, filtering, router);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void runServer(HttpRequestReader httpRequestReader, HttpResponseWriter httpResponseWriter, Filtering filtering, Router router) {
        try {
            HttpRequest httpRequest = httpRequestReader.parseInputStream();

            logger.debug("request method : {}, request url : {}", httpRequest.getRequestLine().getHttpMethod(), httpRequest.getRequestLine().getURL());

            filtering.filter(httpRequest);
            HttpResponse httpResponse = router.route(httpRequest);
            httpResponseWriter.sendResponse(httpResponse);
        } catch (Exception e) {
            httpResponseWriter.sendResponse(exceptionHandler.handleException(e));
        }
    }
}
