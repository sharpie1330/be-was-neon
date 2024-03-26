package webserver.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {
    private Socket clientSocket;
    private ServerSocket listenSocket;
    private PrintWriter printWriter;

    @Test
    @DisplayName("서버에 HTTP 요청을 보내면 응답이 돌아와야 한다.")
    void run() throws IOException {
        // 서버로 보낼 문자열
        String message = """
            POST /user/create HTTP/1.1
            Content-Type: text/plain
            Content-Length: 65
            
            userId=hello&email=hello@gmail.com&nickname=hello&password=1234""";

        send(message);

        Socket socket = listenSocket.accept();
        RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        assertThat(bufferedReader.readLine()).isEqualTo("HTTP/1.1 302 Found");
        assertThat(bufferedReader.readLine()).isEqualTo("Location:/registration/welcome.html");

        socket.close();
        clientSocket.close();
        listenSocket.close();
        printWriter.close();
    }

    private void send(String message) {
        try {
            // 서버가 listen할 소켓 생성
            listenSocket = new ServerSocket(1111);

            // 서버에 연결할 소켓 생성 (서버의 IP 주소와 포트 번호 지정)
            clientSocket = new Socket("localhost", 1111);

            // 소켓으로부터 출력 스트림을 얻음
            printWriter = getPrintWriter(clientSocket, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static PrintWriter getPrintWriter(Socket clientSocket, String message) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();

        // 출력 스트림에 데이터를 쓰기 위해 PrintWriter 사용
        PrintWriter writer = new PrintWriter(outputStream, true);

        // 서버로 문자열 전송
        writer.println(message);
        return writer;
    }
}