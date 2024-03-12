package webserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.MIMEType;

import static utils.StringUtils.*;

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
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder httpHeader = new StringBuilder();

            String line = br.readLine();
            if (line == null) {
                return;
            }

            String[] tokens = line.split(" ");
            String requestMethod = tokens[0];
            String requestUrl = tokens[1];
            logger.debug("request Method : {}, request Url : {}", requestMethod, requestUrl);

            while (!line.isEmpty()) {
                httpHeader.append(appendNewLine(line));
                line = br.readLine();
            }

            DataOutputStream dos = new DataOutputStream(out);
            readFile(dos, requestUrl);
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
            String mimeType = getMimeType(file);
            response200Header(dos, readLen, mimeType);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String getMimeType(File file) {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return MIMEType.getContentType(extension);
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
