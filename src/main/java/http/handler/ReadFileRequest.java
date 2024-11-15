package http.handler;

import configuration.ApplicationConfigs;
import http.core.Request;
import http.core.Response;
import http.interfaces.Handler;
import infrastructure.networking.HTTPStatusCodes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ReadFileRequest extends Handler {
    public ReadFileRequest(Handler next) {
        this.setNext(next);
    }

    @Override
    public Response handle(Request request, Response response) {
        if (response == null) {
            response = new Response(ApplicationConfigs.PROTOCOL, HTTPStatusCodes.OK);
        }

        String directory = ApplicationConfigs.getInstance().getFilesDirectory();
        String fileName = request.getRouteParams().get("filename");

        var file = new File(directory, fileName);
        if (directory.isBlank() || !file.exists()) {
            response.setStatus(HTTPStatusCodes.NOTFOUND);

            return response;
        }

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            response.addHeader("Content-Type", "application/octet-stream");
            response.addHeader("Content-Length", fileContent.length + "");
            response.addBodyContent(new String(fileContent));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (next != null) {
            return next.handle(request, response);
        }

        return response;
    }
}
