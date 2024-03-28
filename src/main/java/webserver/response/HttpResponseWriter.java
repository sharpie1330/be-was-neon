package webserver.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.common.HttpHeader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class HttpResponseWriter {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponseWriter.class);

    private final DataOutputStream dos;

    public HttpResponseWriter(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void sendResponse(HttpResponse httpResponse) {
        final String NEW_LINE = "\r\n";

        try {
            String responseLine = httpResponse.getResponseLine();
            HttpHeader httpHeader = httpResponse.getHeaders();
            byte[] body = httpResponse.getBody();

            // response line
            dos.writeBytes(responseLine);

            // header
            if (httpHeader != null) {
                for (Map.Entry<String, List<String>> header : httpHeader.entrySet()) {
                    dos.writeBytes(header.getKey() + ":" +
                            header.getValue().stream().reduce("", (x, y) -> x.isEmpty() ? y : x + ";" + y) +
                            NEW_LINE);
                }
            }
            dos.writeBytes(NEW_LINE);

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
