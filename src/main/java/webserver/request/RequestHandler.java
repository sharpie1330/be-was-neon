package webserver.request;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.common.HttpHeader;
import webserver.response.ResponseHandler;

import static webserver.utils.PropertyUtils.loadProperties;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String CHARSET = loadProperties().getProperty("charset");

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        try (
                InputStream in = connection.getInputStream();
                OutputStream out = connection.getOutputStream()
        ) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, CHARSET));

            // request line
            String requestLine = br.readLine();
            // 3-way-handshake 처리
            if (requestLine == null) {
                return;
            }

            // header
            List<String> requestHeaders = new ArrayList<>();
            String line;
            while (!(line = br.readLine()).isEmpty()) {
                requestHeaders.add(line);
            }
            HttpHeader httpHeader = new HttpHeader(requestHeaders);

            // body TODO : read, HttpRequest에서 body parse하는 로직 구성
            String requestBody = null;

            // HttpRequest
            HttpRequest httpRequest = HttpRequest.of(requestLine, httpHeader, requestBody);
            logger.debug("request method : {}, request url : {}", httpRequest.getHttpMethod(), httpRequest.getURL());

            // HttpResponse
            ResponseHandler responseHandler = new ResponseHandler(out);
            responseHandler.response(httpRequest);

            // socket close
            connection.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
