package codestargram;

import codestargram.domain.user.requestManager.UserRequestManager;
import codestargram.exception.CustomExceptionHandler;
import codestargram.filter.LoginFilter;
import webserver.WebServer;
import webserver.exception.ExceptionHandler;
import webserver.filter.Filter;

import java.util.List;

public class CodestargramApplication {

    private static final List<Class<?>> CONTROLLER_CLASSES = List.of(UserRequestManager.class);
    private static final List<Filter> FILTERS = List.of(new LoginFilter());
    private static final ExceptionHandler EXCEPTION_HANDLER = new CustomExceptionHandler();

    private CodestargramApplication() {

    }

    public static void main(String[] args) throws Exception {
        WebServer webServer = new WebServer(CONTROLLER_CLASSES, FILTERS, EXCEPTION_HANDLER);
        webServer.startServer(args);
    }
}
