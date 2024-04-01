package codestargram.exception;

import codestargram.exception.user.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.exception.ExceptionHandler;
import webserver.exception.common.ApplicationException;
import webserver.exception.common.ServerException;
import webserver.exception.request.PathNotFoundException;
import webserver.exception.server.BadRequestException;
import webserver.exception.server.UnAuthorizedException;
import webserver.http.type.HttpHeader;
import webserver.http.type.HttpResponse;
import webserver.http.type.HttpStatusCode;
import webserver.http.type.MIMEType;
import webserver.session.Cookie;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CustomExceptionHandler implements ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    public CustomExceptionHandler() {

    }

    @Override
    public HttpResponse handleException(Exception e) {
        if (e instanceof UserAlreadyExistsException) {
            return handleUserAlreadyExistsException();
        }
        if (e instanceof PathNotFoundException) {
            return handlePathNotFoundException();
        }
        if (e instanceof BadRequestException be) {
            return handleBadRequestException(be);
        }
        if (e instanceof UnAuthorizedException uae) {
            return handleUnAuthorizedException(uae);
        }
        if (e instanceof ApplicationException ae) {
            return handleApplicationException(ae);
        }
        if (e instanceof ServerException se) {
            return handleServerException(se);
        }
        return handleOtherException(e);
    }

    private HttpResponse handleUserAlreadyExistsException() {
        return HttpResponse
                .found("/registration/register_failed.html")
                .build();
    }

    private HttpResponse handleBadRequestException(BadRequestException be) {
        logger.error("[BadRequestException] errorMessage : {} | cause Exception : {}",
                be.getErrorMessage(), getExceptionStackTrace(be));

        return HttpResponse
                .found("/error/client_error.html")
                .build();
    }

    private HttpResponse handlePathNotFoundException() {
        return HttpResponse
                .found("/error/notfound.html")
                .build();
    }

    private HttpResponse handleUnAuthorizedException(UnAuthorizedException uae) {
        Cookie cookie = new Cookie();
        cookie.setCookie("redirectUrl", uae.getErrorMessage());

        return HttpResponse
                .found("/login")
                .header(HttpHeader.SET_COOKIE, cookie.toString())
                .build();
    }

    private HttpResponse handleServerException(ServerException se) {
        logger.error("[ServerException] errorMessage : {} | cause Exception : {}",
                se.getErrorMessage(), getExceptionStackTrace(se));

        byte[] responseBody = createResponseBody(se.getHttpStatusCode(), se.getErrorMessage());

        return errorResponse(se.getHttpStatusCode(), MIMEType.html, responseBody);
    }

    private HttpResponse handleApplicationException(ApplicationException ae) {
        logger.error("[ApplicationException] errorMessage : {} | cause Exception : {}",
                ae.getErrorMessage(), getExceptionStackTrace(ae));

        byte[] responseBody = createResponseBody(ae.getHttpStatusCode(), ae.getErrorMessage());

        return errorResponse(ae.getHttpStatusCode(), MIMEType.html, responseBody);
    }

    private HttpResponse handleOtherException(Exception e) {
        logger.error("[Exception] errorMessage : {} | cause Exception : {}",
                e.getMessage(), getExceptionStackTrace(e));

        return HttpResponse
                .found("/error/server_error.html")
                .build();
    }

    private HttpResponse errorResponse(HttpStatusCode httpStatusCode, MIMEType mimeType, byte[] responseBody) {
        return HttpResponse
                .status(httpStatusCode)
                .contentType(mimeType)
                .contentLength(responseBody.length)
                .body(responseBody);
    }

    private byte[] createResponseBody(HttpStatusCode httpStatusCode, String errorMessage) {
        String status =
                """
                   <!DOCTYPE html>
                   <html lang="ko">
                     <head>
                       <meta charset="UTF-8" />
                       <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                       <title>""" + httpStatusCode.getMessage() + "</title>\n</head>\n" +
                        "<body>\n" +
                        "<h1 style=\"text-align: center;\">" + httpStatusCode.getMessage() + "</h1>\n" +
                        "<h3 style=\"text-align: center;\">" + errorMessage + "</h3>\n" +
                        "</body>\n</html>";
        return status.getBytes();
    }

    private String getExceptionStackTrace(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return String.valueOf(stringWriter);
    }
}
