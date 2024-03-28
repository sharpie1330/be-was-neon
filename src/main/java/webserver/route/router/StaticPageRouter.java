package webserver.route.router;

import webserver.exception.common.InternalServerErrorException;
import webserver.exception.server.PathNotFoundException;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.type.MIMEType;
import webserver.utils.URLUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StaticPageRouter {
    private static final StaticPageRouter instance = new StaticPageRouter();

    private StaticPageRouter() {

    }

    public static StaticPageRouter getInstance() {
        return instance;
    }

    public HttpResponse routeStaticPage(HttpRequest httpRequest, String filePath) {
        File file = new File(filePath);

        try (FileInputStream fileIn = new FileInputStream(file)) {
            byte[] body = new byte[(int) file.length()];
            int readLen = fileIn.read(body);
            MIMEType mimeType = MIMEType.getMimeType(URLUtils.getExtension(filePath));
            return HttpResponse
                    .ok(httpRequest.getRequestLine().getVersion())
                    .contentLength(readLen)
                    .contentType(mimeType)
                    .body(body);
        } catch (FileNotFoundException e) {
            throw new PathNotFoundException();
        } catch (IOException e) {
            throw new InternalServerErrorException(e.getMessage(), e);
        }
    }
}
