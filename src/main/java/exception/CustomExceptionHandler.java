package exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.response.ResponseHandler;
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
        logger.error("[CustomException] url : {} | errorType : {} | errorMessage : {} | cause Exception : {}",
                httpRequest.getURL(), ce.getCustomErrorType(), ce.getErrorMessage(), ce.getCause());

        byte[] jsonBody = createJsonBody(ce.getCustomErrorType());

        return HttpResponse
                .status(httpRequest.getVersion(), ce.getCustomErrorType().getHttpStatusCode())
                .contentType(MIMEType.json)
                .contentLength(jsonBody.length)
                .body(jsonBody);
    }

    private HttpResponse handleOtherException(Exception e, HttpRequest httpRequest) {
        logger.error("[Exception] url : {} | errorMessage : {} | cause Exception : {}",
                httpRequest.getURL(), e.getMessage(), e.getCause());

        byte[] jsonBody = createJsonBody(CustomErrorType.SERVER_ERROR);

        return HttpResponse
                .internalServerError(httpRequest.getVersion())
                .contentType(MIMEType.json)
                .contentLength(jsonBody.length)
                .body(jsonBody);
    }

    private byte[] createJsonBody(CustomErrorType customErrorType) {
        String json = String.format(
                """
                        {
                            "errorCode":"%s",
                            "errorMessage":"%s"
                        }
                        """,
                customErrorType.getCode(),
                customErrorType.getErrorMessage()
        );

        try {
            return json.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            throw new CustomException(CustomErrorType.SERVER_ERROR);
        }
    }
}
