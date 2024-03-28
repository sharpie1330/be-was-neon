package webserver;

import exception.CustomExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.HttpRequest;
import webserver.request.HttpRequestReader;
import webserver.response.HttpResponse;
import webserver.response.HttpResponseWriter;
import webserver.route.Route;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WebApplicationServer implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(WebApplicationServer.class);

    private final InputStream in;
    private final OutputStream out;

    public WebApplicationServer(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try (in; out) {
            runServer();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void runServer() {
        HttpRequestReader httpRequestReader = new HttpRequestReader(in);
        HttpResponseWriter httpResponseWriter = new HttpResponseWriter(out);
        Route router = Route.getInstance();
        CustomExceptionHandler exceptionHandler = new CustomExceptionHandler();

        try {
            HttpRequest httpRequest = httpRequestReader.parseInputStream();
            HttpResponse httpResponse = router.route(httpRequest);
            httpResponseWriter.sendResponse(httpResponse);
        } catch (Exception e) {
            httpResponseWriter.sendResponse(exceptionHandler.handleException(e));
        }
    }
}
