package webserver.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.exception.common.ApplicationException;
import webserver.exception.common.ServerException;
import webserver.exception.server.BadRequestException;
import webserver.exception.server.MethodNotAllowedException;
import webserver.exception.server.NotFoundException;
import webserver.http.type.HttpResponse;
import webserver.http.type.HttpStatusCode;
import webserver.http.type.MIMEType;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DefaultExceptionHandler implements ExceptionHandler{
    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    public DefaultExceptionHandler() {

    }

    @Override
    public HttpResponse handleException(Exception e) {
        if (e instanceof NotFoundException ne) {
            return handleNotFoundException(ne);
        }
        if (e instanceof BadRequestException be) {
            return handleBadRequestException(be);
        }
        if (e instanceof MethodNotAllowedException me) {
            return handleMethodNotAllowedException(me);
        }
        if (e instanceof ApplicationException ae) {
            return handleApplicationException(ae);
        }
        if (e instanceof ServerException se) {
            return handleServerException(se);
        }
        return handleOtherException(e);
    }

    private HttpResponse handleNotFoundException(NotFoundException ne) {
        logger.error("[NotFoundException] errorMessage : {} | cause Exception : {}",
                ne.getErrorMessage(), getExceptionStackTrace(ne));

        byte[] responseBody = createResponseBody(HttpStatusCode.NOT_FOUND, ne.getErrorMessage());

        return errorResponse(HttpStatusCode.NOT_FOUND, MIMEType.html, responseBody);
    }

    private HttpResponse handleBadRequestException(BadRequestException be) {
        logger.error("[BadRequestException] errorMessage : {} | cause Exception : {}",
                be.getErrorMessage(), getExceptionStackTrace(be));

        byte[] responseBody = createResponseBody(HttpStatusCode.BAD_REQUEST, be.getErrorMessage());

        return errorResponse(HttpStatusCode.BAD_REQUEST, MIMEType.html, responseBody);
    }

    private HttpResponse handleMethodNotAllowedException(MethodNotAllowedException me) {
        logger.error("[MethodNotAllowedException] errorMessage : {} | cause Exception : {}",
                me.getErrorMessage(), getExceptionStackTrace(me));

        byte[] responseBody = createResponseBody(HttpStatusCode.METHOD_NOT_ALLOWED, me.getErrorMessage());

        return errorResponse(HttpStatusCode.METHOD_NOT_ALLOWED, MIMEType.html, responseBody);
    }

    private HttpResponse handleServerException(ServerException ce) {
        logger.error("[ServerException] errorMessage : {} | cause Exception : {}",
                ce.getErrorMessage(), getExceptionStackTrace(ce));

        byte[] responseBody = createResponseBody(ce.getHttpStatusCode(), ce.getErrorMessage());

        return errorResponse(ce.getHttpStatusCode(), MIMEType.html, responseBody);
    }

    private HttpResponse handleApplicationException(ApplicationException se) {
        logger.error("[ApplicationException] errorMessage : {} | cause Exception : {}",
                se.getErrorMessage(), getExceptionStackTrace(se));

        byte[] responseBody = createResponseBody(se.getHttpStatusCode(), se.getErrorMessage());

        return errorResponse(se.getHttpStatusCode(), MIMEType.html, responseBody);
    }

    private HttpResponse handleOtherException(Exception e) {
        logger.error("[Exception] errorMessage : {} | cause Exception : {}",
                e.getMessage(), getExceptionStackTrace(e));

        byte[] responseBody = createResponseBody(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());

        return errorResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, MIMEType.html, responseBody);
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
