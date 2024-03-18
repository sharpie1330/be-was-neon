package exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.response.ResponseHandler;

public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    public CustomExceptionHandler() {

    }

    public HttpResponse handleException(Exception e, HttpRequest httpRequest) {
        if (e instanceof CustomException ce) {
            return handleCustomException(ce, httpRequest);
        } else {
            logger.error("[Exception] url : {} | errorMessage : {} | cause Exception : {}",
                    httpRequest.getURL(), e.getMessage(), e.getCause());

            return HttpResponse
                    .internalServerError(httpRequest.getVersion())  // TODO: body 추가
                    .build();
        }
    }

    private HttpResponse handleCustomException(CustomException ce, HttpRequest httpRequest) {
        logger.error("[CustomException] url : {} | errorType : {} | errorMessage : {} | cause Exception : {}",
                httpRequest.getURL(), ce.getCustomExceptionType(), ce.getErrorMessage(), ce.getCause());

        return HttpResponse
                .status(httpRequest.getVersion(), ce.getCustomExceptionType().getHttpStatusCode()) // TODO: body 추가
                .build();
    }
}
