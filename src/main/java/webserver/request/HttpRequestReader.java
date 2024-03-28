package webserver.request;

import webserver.common.HttpBody;
import webserver.common.HttpHeader;
import webserver.common.HttpRequestLine;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

public class HttpRequestReader {

    private final BufferedInputStream bis;

    public HttpRequestReader(InputStream in) {
        this.bis = new BufferedInputStream(in);
    }

    public HttpRequest parseInputStream() throws IOException, TooManyListenersException{
        // request line
        String requestLine = readLine();
        // 3-way-handshake 처리
        if (requestLine.isEmpty()) {
            return null;
        }
        HttpRequestLine httpRequestLine = new HttpRequestLine(requestLine);

        // header
        HttpHeader httpHeader = readHeader();

        // body
        int contentLength;
        try {
            contentLength = httpHeader.getContentLength();
        } catch (NumberFormatException e) {
            throw new TooManyListenersException();
        }
        HttpBody httpBody = contentLength > 0 ? readBody(contentLength) : new HttpBody();

        return new HttpRequest(httpRequestLine, httpHeader, httpBody);
    }

    /**
     * 한 줄을 읽어 반환한다
     * @return carriage return을 제외하고 newLine 이전 문자를 모두 읽어 문자열로 반환한다
     * @throws IOException
     */
    private String readLine() throws IOException {
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
     * @return 헤더 객체를 생성해 반환
     * @throws IOException
     */
    private HttpHeader readHeader() throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        String line;
        while (!(line = readLine()).isEmpty()) {
            requestHeaders.add(line);
        }
        return HttpHeader.of(requestHeaders);
    }

    /**
     * 버퍼 크기 만큼씩 읽어 읽은 바이트를 각각 char 타입으로 바꾸어 String으로 저장한다.
     * 저장된 String으로 HttpBody 객체를 만들어 반환한다. 헤더에 contentLength 정보가 없으면 빈 body 객체가 생성된다
     * @param contentLength 헤더의 Content-Length (int)
     * @return HttpBody 객체를 만들어 반환
     * @throws IOException
     */
    private HttpBody readBody(int contentLength) throws IOException {
        byte[] body = new byte[contentLength];
        byte[] buffer = new byte[1024];
        int bytesRead;
        while (contentLength > 0L) {
            if((bytesRead = bis.read(buffer, 0, Math.min(buffer.length, contentLength))) == -1) break;
            System.arraycopy(buffer, 0, body, 0, bytesRead);
            contentLength -= bytesRead;
        }
        return new HttpBody(body);
    }
}
