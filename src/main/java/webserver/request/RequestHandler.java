package webserver.request;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.common.HttpBody;
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
            BufferedInputStream bis = new BufferedInputStream(in);

            // request line
            String requestLine = readLine(bis);
            // 3-way-handshake 처리
            if (requestLine.isEmpty()) {
                return;
            }

            // TODO: 유효한 메서드인지, HTTP 프로토콜이 맞는지 확인하는 로직 추가, 헤더가 없는 경우 체크?

            // header
            List<String> requestHeaders = new ArrayList<>();
            String line;
            while (!(line = readLine(bis)).isEmpty()) {
                requestHeaders.add(line);
            }
            HttpHeader httpHeader = HttpHeader.of(requestHeaders);

            // Content-Length
            long contentLength = httpHeader.getContentLength();

            // body
            StringBuilder requestBodyBuilder = new StringBuilder();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while (contentLength > 0L) {
                if((bytesRead = bis.read(buffer, 0, (int) Math.min(buffer.length, contentLength))) == -1) break;
                String chunk = new String(buffer, 0, bytesRead, CHARSET);
                requestBodyBuilder.append(chunk);
                contentLength -= bytesRead;
            }
            HttpBody requestBody = HttpBody.of(requestBodyBuilder.toString());

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

    private String readLine(BufferedInputStream bis) throws IOException {
        StringBuilder sb = new StringBuilder();
        int read;
        while ((read = bis.read()) != -1) {
            char readChar = (char) read;
            if (readChar == '\r') {
                continue;
            }

            if (readChar == '\n') {
                break;
            }

            sb.append(readChar);
        }
        return sb.toString();
    }
}
