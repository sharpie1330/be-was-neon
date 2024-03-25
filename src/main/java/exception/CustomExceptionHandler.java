package exception;

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
        if (e instanceof CustomException ce) {
            return handleCustomException(ce);
        }
        return handleOtherException(e);
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

        byte[] jsonBody = createJsonBody(new CustomException(HttpStatusCode.INTERNAL_SERVER_ERROR));

        return HttpResponse
                .internalServerError("HTTP/1.1")
                .contentType(MIMEType.json)
                .contentLength(jsonBody.length)
                .body(jsonBody);
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
