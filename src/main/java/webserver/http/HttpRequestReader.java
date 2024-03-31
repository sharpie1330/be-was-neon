package webserver.http;

import webserver.exception.request.InvalidHttpVersionException;
import webserver.exception.request.InvalidRequestLineFormatException;
import webserver.exception.request.TooLargeInputException;
import webserver.exception.server.MethodNotAllowedException;
import webserver.http.type.*;
import webserver.type.*;
import webserver.utils.Delimiter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestReader {

    private final BufferedInputStream bis;

    public HttpRequestReader(InputStream in) {
        this.bis = new BufferedInputStream(in);
    }

    /**
     * Input Stream을 읽어 HttpRequest 인스턴스를 생성해 반환
     * @return HttpRequest
     * @throws IOException BufferedInputStream으로 read()할 때 발생할 수 있는 입출력 예외
     * @throws TooLargeInputException content-length 값이 Integer.MAX_VALUE 값을 초과하는 경우 발생
     * @throws MethodNotAllowedException request line을 읽을 때 허용되지 않은 Http Method인 경우 발생
     */
    public HttpRequest parseInputStream() throws IOException, MethodNotAllowedException, TooLargeInputException {
        // request line
        String requestLine = readLine();
        // 3-way-handshake 처리
        if (requestLine.isEmpty()) {
            return null;
        }
        HttpRequestLine httpRequestLine = readRequestLine(requestLine);

        // header
        HttpHeader httpHeader = readHeader();

        // body
        int contentLength;
        try {
            contentLength = httpHeader.getContentLength();
        } catch (NumberFormatException e) {
            throw new TooLargeInputException();
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
     * Http request의 첫 줄인 request line을 해석해 HttpRequestLine 인스턴스를 생성해 반환
     * @param line input stream에서 읽은 첫 줄
     * @return line을 해석해 HttpRequestLine을 생성해 반환
     * @throws MethodNotAllowedException 허용되지 않은 HTTP Method이면 예외 발생
     */
    private HttpRequestLine readRequestLine(String line) throws MethodNotAllowedException{
        String[] split = line.split(Delimiter.SPACE);

        if (split.length != 3) {
            throw new InvalidRequestLineFormatException();
        }

        HttpMethod httpMethod = HttpMethod.get(split[0]);
        if (httpMethod == null) {
            throw new MethodNotAllowedException();
        }

        String URL = split[1];

        String version = split[2];
        if (!version.equals("HTTP/1.1")) {
            throw new InvalidHttpVersionException();
        }

        return new HttpRequestLine(httpMethod, URL, version);
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
