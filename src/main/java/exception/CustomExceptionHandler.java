package exception;

import exception.common.BadRequestException;
import exception.common.MethodNotAllowedException;
import exception.common.NotFoundException;
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

public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private static final String CHARSET = loadProperties().getProperty("charset");

    public CustomExceptionHandler() {

    }

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
        if (e instanceof CustomException ce) {
            return handleCustomException(ce);
        }
        return handleOtherException(e);
    }

    private HttpResponse handleNotFoundException(NotFoundException ne) {
        logger.error("[NotFoundException] errorCode : {} | errorMessage : {} | cause Exception : {}",
                ne.getErrorCode(), ne.getErrorMessage(), getExceptionStackTrace(ne));

        return HttpResponse
                .status("HTTP/1.1", HttpStatusCode.FOUND)
                .location("/error/notfound.html")
                .build();
    }

    private HttpResponse handleBadRequestException(BadRequestException be) {
        logger.error("[BadRequestException] errorCode : {} | errorMessage : {} | cause Exception : {}",
                be.getErrorCode(), be.getErrorMessage(), getExceptionStackTrace(be));

        return HttpResponse
                .status("HTTP/1.1", HttpStatusCode.FOUND)
                .location("/error/client_error.html")
                .build();
    }

    private HttpResponse handleMethodNotAllowedException(MethodNotAllowedException me) {
        logger.error("[MethodNotAllowedException] errorCode : {} | errorMessage : {} | cause Exception : {}",
                me.getErrorCode(), me.getErrorMessage(), getExceptionStackTrace(me));

        return HttpResponse
                .status("HTTP/1.1", HttpStatusCode.FOUND)
                .location("/error/client_error.html")
                .build();
    }

    private HttpResponse handleCustomException(CustomException ce) {
        logger.error("[CustomException] errorCode : {} | errorMessage : {} | cause Exception : {}",
                ce.getErrorCode(), ce.getErrorMessage(), getExceptionStackTrace(ce));

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

    private byte[] createJsonBody(CustomException customException) {
        String json = String.format(
                """
                        {
                            "errorCode":"%s",
                            "errorMessage":"%s"
                        }
                        """,
                customException.getErrorCode(),
                customException.getErrorMessage()
        );

        try {
            return json.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            throw new CustomException(HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String getExceptionStackTrace(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return String.valueOf(stringWriter);
    }
}
