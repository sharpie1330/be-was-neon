package webserver.request;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.MIMEType;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            // request line
            String requestLine = br.readLine();

            // header
            List<String> requestHeaders = new ArrayList<>();
            String line;
            while (!(line = br.readLine()).isEmpty()) {
                requestHeaders.add(line);
            }

            // body TODO : read, HttpRequest에서 body parse하는 로직 구성
            String requestBody = null;

            HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeaders, requestBody);
            logger.debug("request method : {}, request url : {}", httpRequest.getHttpMethod(), httpRequest.getURL());

            DataOutputStream dos = new DataOutputStream(out);
            readFile(dos, httpRequest.getURL());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void readFile(DataOutputStream dos, String requestUrl) {
        final String DEFAULT_PATH = "src/main/resources/static";
        File file = new File(DEFAULT_PATH.concat(requestUrl));

        try (FileInputStream fileIn = new FileInputStream(file)){
            byte[] body = new byte[(int) file.length()];
            int readLen = fileIn.read(body);
            String mimeType = getMimeType(file.getName());
            response200Header(dos, readLen, mimeType);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String getMimeType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return MIMEType.getMimeType(extension);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String mimeType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + mimeType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
