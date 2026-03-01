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

        if (directory == null || directory.isBlank()) {
            response.setStatus(HTTPStatusCodes.NOT_FOUND);
            return response;
        }

        var file = new File(directory, fileName);
        try {
            File canonicalBase = new File(directory).getCanonicalFile();
            File canonicalFile = file.getCanonicalFile();
            if (!canonicalFile.toPath().startsWith(canonicalBase.toPath()) || !canonicalFile.exists()) {
                response.setStatus(HTTPStatusCodes.NOT_FOUND);
                return response;
            }
        } catch (IOException e) {
            response.setStatus(HTTPStatusCodes.NOT_FOUND);
            return response;
        }

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            response.addHeader("Content-Type", "application/octet-stream");
            response.addHeader("Content-Length", fileContent.length + "");
            response.setBodyBytes(fileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (next != null) {
            return next.handle(request, response);
        }

        return response;
    }
}
