package webserver.request;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.common.HttpBody;
import webserver.common.HttpHeader;
import webserver.common.HttpRequestLine;
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
            HttpRequestLine httpRequestLine = new HttpRequestLine(requestLine);

            // header
            HttpHeader httpHeader = readHeader(bis);

            // body
            int contentLength = httpHeader.getContentLength();
            HttpBody httpBody = readBody(bis, contentLength);

            // HttpRequest
            HttpRequest httpRequest = new HttpRequest(httpRequestLine, httpHeader, httpBody);

            // HttpResponse
            ResponseHandler responseHandler = new ResponseHandler(out);
            responseHandler.response(httpRequest);

            // socket close
            connection.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 한 줄을 읽어 반환한다
     * @param bis connection의 InputStream을 버퍼링
     * @return carriage return을 제외하고 newLine 이전 문자를 모두 읽어 문자열로 반환한다
     * @throws IOException
     */
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

    /**
     * 한 줄씩 읽어 리스트에 저장하고, 해당 리스트를 헤더 생성자에 전달해 헤더 객체를 생성한다
     * @param bis BufferedInputStream
     * @return 헤더 객체를 생성해 반환
     * @throws IOException
     */
    private HttpHeader readHeader(BufferedInputStream bis) throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        String line;
        while (!(line = readLine(bis)).isEmpty()) {
            requestHeaders.add(line);
        }
        return HttpHeader.of(requestHeaders);
    }

    /**
     * 버퍼 크기 만큼씩 읽어 읽은 바이트를 각각 char 타입으로 바꾸어 String으로 저장한다.
     * 저장된 String으로 HttpBody 객체를 만들어 반환한다. 헤더에 contentLength 정보가 없으면 빈 body 객체가 생성된다
     * @param bis BufferedInputStream
     * @param contentLength 헤더의 Content-Length (int)
     * @return HttpBody 객체를 만들어 반환
     * @throws IOException
     */
    private HttpBody readBody(BufferedInputStream bis, int contentLength) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while (contentLength > 0L) {
            if((bytesRead = bis.read(buffer, 0, Math.min(buffer.length, contentLength))) == -1) break;
            for (int i = 0; i < bytesRead; i++) {
                stringBuilder.append((char) buffer[i]);
            }
            contentLength -= bytesRead;
        }

        return new HttpBody(stringBuilder.toString());
    }
}
