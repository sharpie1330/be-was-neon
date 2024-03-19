package webserver.response;

import exception.CustomExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.HttpRequest;
import webserver.route.Route;

import java.io.*;
import java.util.List;
import java.util.Map;

public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    private final DataOutputStream dos;
    private final CustomExceptionHandler customExceptionHandler;

    public ResponseHandler(OutputStream out) {
        this.dos = new DataOutputStream(out);
        this.customExceptionHandler = new CustomExceptionHandler();
    }

    public void response(HttpRequest httpRequest) {
        try {
            HttpResponse httpResponse = Route.getInstance().route(httpRequest);
            sendResponse(httpResponse);
        } catch (Exception e) {
            sendResponse(customExceptionHandler.handleException(e, httpRequest));
        }
    }

    public void sendResponse(HttpResponse httpResponse) {
        final String NEW_LINE = "\r\n";

        try {
            String responseLine = httpResponse.getResponseLine();
            Map<String, List<String>> headerMap = httpResponse.getHeaders();
            byte[] body = httpResponse.getBody();

            // response line
            dos.writeBytes(responseLine);

            // header
            if (headerMap != null) {
                for (Map.Entry<String, List<String>> header : headerMap.entrySet()) {
                    dos.writeBytes(header.getKey() + ":" +
                            header.getValue().stream().reduce("", (x, y) -> x.isEmpty() ? y : x + ";" + y) +
                            NEW_LINE);
                }
                dos.writeBytes(NEW_LINE);
            }

            // body
            if (body != null) {
                dos.write(body, 0, body.length);
            }

            dos.flush();
        } catch (IOException e) {
            logger.error("응답을 보내는 중 에러가 발생했습니다 : {}", e.getMessage());
        }
    }
}
