package webserver.response;

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

    public ResponseHandler(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void response(HttpRequest httpRequest) {
        final String NEW_LINE = "\r\n";

        HttpResponse httpResponse = Route.route(httpRequest);
        String responseLine = httpResponse.getResponseLine();
        Map<String, List<String>> headerMap = httpResponse.getHeader();
        byte[] body = httpResponse.getBody();

        try {
            // start line
            dos.writeBytes(responseLine);

            // header
            for (Map.Entry<String, List<String>> header : headerMap.entrySet()) {
                dos.writeBytes(header.getKey() + ":" +
                        header.getValue().stream().reduce("", (x, y) -> x.isEmpty() ? y : x + ";" + y) + NEW_LINE);
            }
            dos.writeBytes(NEW_LINE);

            // body
            if (body != null) {
                dos.write(body, 0, body.length);
            }

            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
