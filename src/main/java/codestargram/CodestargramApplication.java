package codestargram;

import user.requestManager.UserRequestManager;
import webserver.WebServer;

import java.util.List;

public class CodestargramApplication {

    private static final List<Class<?>> CONTROLLER_CLASSES = List.of(UserRequestManager.class);
//    private static final ExceptionHandler EXCEPTION_HANDLER = {구현 시 입력};

    private CodestargramApplication() {

    }

    public static void main(String[] args) throws Exception {
        WebServer webServer = new WebServer(CONTROLLER_CLASSES);
        webServer.startServer(args);
    }
}
