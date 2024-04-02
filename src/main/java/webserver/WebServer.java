package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.exception.ExceptionHandler;
import webserver.utils.PropertyUtils;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = PropertyUtils.loadPortFromProperties();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private final List<Class<?>> controllers;
    private ExceptionHandler exceptionHandler;

    public WebServer(List<Class<?>> controllers) {
        this.controllers = controllers;
    }

    public WebServer(List<Class<?>> controllers, ExceptionHandler exceptionHandler) {
        this.controllers = controllers;
        this.exceptionHandler = exceptionHandler;
    }

    public void startServer(String[] args) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                executorService.execute(
                        new WebApplicationServer(
                                connection.getInputStream(), connection.getOutputStream(), controllers, exceptionHandler
                        )
                );
            }
        }
    }
}
