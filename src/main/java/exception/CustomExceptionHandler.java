package exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.response.ResponseHandler;
import webserver.type.HttpStatusCode;
import webserver.type.MIMEType;

import java.io.UnsupportedEncodingException;

import static webserver.utils.PropertyUtils.loadProperties;

public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private static final String CHARSET = loadProperties().getProperty("charset");

    public CustomExceptionHandler() {

    }

    public HttpResponse handleException(Exception e, HttpRequest httpRequest) {
        if (e instanceof CustomException ce) {
            return handleCustomException(ce, httpRequest);
        }
        return handleOtherException(e, httpRequest);
    }

    private HttpResponse handleCustomException(CustomException ce, HttpRequest httpRequest) {
        logger.error("[CustomException] url : {} | errorCode : {} | errorMessage : {} | cause Exception : {}",
                httpRequest.getRequestLine().getURL(), ce.getErrorCode(), ce.getErrorMessage(), ce.getCause());

        byte[] jsonBody = createJsonBody(ce);

        return HttpResponse
                .status(httpRequest.getRequestLine().getVersion(), ce.getHttpStatusCode())
                .contentType(MIMEType.json)
                .contentLength(jsonBody.length)
                .body(jsonBody);
    }

    private HttpResponse handleOtherException(Exception e, HttpRequest httpRequest) {
        logger.error("[Exception] url : {} | errorMessage : {} | cause Exception : {}",
                httpRequest.getRequestLine().getURL(), e.getMessage(), e.getCause());

        byte[] jsonBody = createJsonBody(new CustomException(HttpStatusCode.INTERNAL_SERVER_ERROR));

        return HttpResponse
                .internalServerError(httpRequest.getRequestLine().getVersion())
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
}
