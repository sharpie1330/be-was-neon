package webserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        final String DEFAULT_PATH = "src/main/resources/static/";
        final String INDEX_HTML = "index.html";
        File file = new File(DEFAULT_PATH.concat(INDEX_HTML));

        try (
                InputStream in = connection.getInputStream();
                OutputStream out = connection.getOutputStream();
                FileInputStream fileIn = new FileInputStream(file)
        ) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder httpHeader = new StringBuilder();

            String line = br.readLine();
            if (line == null) {
                return;
            }

            String[] tokens = line.split(" ");
            logger.debug("request Method : {}, request Url : {}", tokens[0], tokens[1]);

            while (!line.isEmpty()) {
                httpHeader.append(appendNewLine(line));
                line = br.readLine();
            }

            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = new byte[(int) file.length()];
            int readLen = fileIn.read(body);

            response200Header(dos, readLen);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
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
