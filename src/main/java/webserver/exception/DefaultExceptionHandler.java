package webserver.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.exception.common.ApplicationException;
import webserver.exception.common.ServerException;
import webserver.exception.server.BadRequestException;
import webserver.exception.server.MethodNotAllowedException;
import webserver.exception.server.NotFoundException;
import webserver.response.HttpResponse;
import webserver.type.HttpStatusCode;
import webserver.type.MIMEType;

import java.io.PrintWriter;
import java.io.StringWriter;

import static webserver.WebServer.HTTP_VERSION;

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

        byte[] responseBody = createResponseBody(HttpStatusCode.NOT_FOUND);

        return errorResponse(HttpStatusCode.NOT_FOUND, MIMEType.html, responseBody);
    }

    private HttpResponse handleBadRequestException(BadRequestException be) {
        logger.error("[BadRequestException] errorMessage : {} | cause Exception : {}",
                be.getErrorMessage(), getExceptionStackTrace(be));

        byte[] responseBody = createResponseBody(HttpStatusCode.BAD_REQUEST);

        return errorResponse(HttpStatusCode.BAD_REQUEST, MIMEType.html, responseBody);
    }

    private HttpResponse handleMethodNotAllowedException(MethodNotAllowedException me) {
        logger.error("[MethodNotAllowedException] errorMessage : {} | cause Exception : {}",
                me.getErrorMessage(), getExceptionStackTrace(me));

        byte[] responseBody = createResponseBody(HttpStatusCode.METHOD_NOT_ALLOWED);

        return errorResponse(HttpStatusCode.METHOD_NOT_ALLOWED, MIMEType.html, responseBody);
    }

    private HttpResponse handleServerException(ServerException ce) {
        logger.error("[ServerException] errorMessage : {} | cause Exception : {}",
                ce.getErrorMessage(), getExceptionStackTrace(ce));

        byte[] responseBody = createResponseBody(ce.getHttpStatusCode());

        return errorResponse(ce.getHttpStatusCode(), MIMEType.html, responseBody);
    }

    private HttpResponse handleApplicationException(ApplicationException se) {
        logger.error("[ApplicationException] errorMessage : {} | cause Exception : {}",
                se.getErrorMessage(), getExceptionStackTrace(se));

        byte[] responseBody = createResponseBody(se.getHttpStatusCode());

        return errorResponse(se.getHttpStatusCode(), MIMEType.html, responseBody);
    }

    private HttpResponse handleOtherException(Exception e) {
        logger.error("[Exception] errorMessage : {} | cause Exception : {}",
                e.getMessage(), getExceptionStackTrace(e));

        byte[] responseBody = createResponseBody(HttpStatusCode.INTERNAL_SERVER_ERROR);

        return errorResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, MIMEType.html, responseBody);
    }

    private HttpResponse errorResponse(HttpStatusCode httpStatusCode, MIMEType mimeType, byte[] responseBody) {
        return HttpResponse
                .status(HTTP_VERSION, httpStatusCode)
                .contentType(mimeType)
                .contentLength(responseBody.length)
                .body(responseBody);
    }

    private byte[] createResponseBody(HttpStatusCode httpStatusCode) {
        String status = "<h1 style=\"text-align: center;\">" + httpStatusCode.getMessage() + "</h1>";
        return status.getBytes();
    }

    private String getExceptionStackTrace(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return String.valueOf(stringWriter);
    }
}
