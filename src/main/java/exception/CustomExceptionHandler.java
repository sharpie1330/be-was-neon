package exception;

import webserver.exception.ExceptionHandler;
import webserver.exception.common.ServerException;
import webserver.exception.server.BadRequestException;
import webserver.exception.server.InternalServerErrorException;
import webserver.exception.server.MethodNotAllowedException;
import webserver.exception.server.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.response.HttpResponse;
import webserver.response.ResponseHandler;
import webserver.type.HttpStatusCode;
import webserver.type.MIMEType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import static webserver.utils.PropertyUtils.loadProperties;

public class CustomExceptionHandler implements ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private static final String CHARSET = loadProperties().getProperty("charset");

    public CustomExceptionHandler() {

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
        if (e instanceof ServerException ce) {
            return handleCustomException(ce);
        }
        return handleOtherException(e);
    }

    private HttpResponse handleNotFoundException(NotFoundException ne) {
        logger.error("[NotFoundException] errorMessage : {} | cause Exception : {}",
                ne.getErrorMessage(), getExceptionStackTrace(ne));

        return HttpResponse
                .status("HTTP/1.1", HttpStatusCode.FOUND)
                .location("/error/notfound.html")
                .build();
    }

    private HttpResponse handleBadRequestException(BadRequestException be) {
        logger.error("[BadRequestException] errorMessage : {} | cause Exception : {}",
                be.getErrorMessage(), getExceptionStackTrace(be));

        return HttpResponse
                .status("HTTP/1.1", HttpStatusCode.FOUND)
                .location("/error/client_error.html")
                .build();
    }

    private HttpResponse handleMethodNotAllowedException(MethodNotAllowedException me) {
        logger.error("[MethodNotAllowedException] errorMessage : {} | cause Exception : {}",
                me.getErrorMessage(), getExceptionStackTrace(me));

        return HttpResponse
                .status("HTTP/1.1", HttpStatusCode.FOUND)
                .location("/error/client_error.html")
                .build();
    }

    private HttpResponse handleCustomException(ServerException ce) {
        logger.error("[CustomException] errorMessage : {} | cause Exception : {}",
                ce.getErrorMessage(), getExceptionStackTrace(ce));

        byte[] jsonBody = createJsonBody(ce);

        return HttpResponse
                .status("HTTP/1.1", ce.getHttpStatusCode())
                .contentType(MIMEType.json)
                .contentLength(jsonBody.length)
                .body(jsonBody);
    }

    private HttpResponse handleOtherException(Exception e) {
        logger.error("[Exception] errorMessage : {} | cause Exception : {}",
                e.getMessage(), getExceptionStackTrace(e));

        return HttpResponse
                .status("HTTP/1.1", HttpStatusCode.FOUND)
                .location("/error/server_error.html")
                .build();
    }

    private byte[] createJsonBody(ServerException serverException) {
        String json = String.format(
                """
                        {
                            "errorMessage":"%s"
                        }
                        """,
                serverException.getErrorMessage()
        );

        try {
            return json.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new InternalServerErrorException(e.getMessage(), e);
        }
    }

    private String getExceptionStackTrace(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return String.valueOf(stringWriter);
    }
}
