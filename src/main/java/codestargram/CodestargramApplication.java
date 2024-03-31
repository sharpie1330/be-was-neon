package codestargram;

import codestargram.domain.user.requestManager.UserRequestManager;
import codestargram.filter.LoginFilter;
import webserver.WebServer;
import webserver.filter.Filter;

import java.util.List;

public class CodestargramApplication {

    private static final List<Class<?>> CONTROLLER_CLASSES = List.of(UserRequestManager.class);
//    private static final ExceptionHandler EXCEPTION_HANDLER = {구현 시 입력};
    private static final List<Filter> FILTERS = List.of(new LoginFilter());

    private CodestargramApplication() {

    }

    public static void main(String[] args) throws Exception {
        WebServer webServer = new WebServer(CONTROLLER_CLASSES, FILTERS);
        webServer.startServer(args);
    }
}
