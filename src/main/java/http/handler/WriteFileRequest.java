// WriteFileRequest.java
//
// Creates new files in the configured directory from the request body.
// Validates the target path against the base directory to prevent
// path traversal. Only creates files that don't already exist.

package http.handler;

import configuration.ApplicationConfigs;
import http.core.Request;
import http.core.Response;
import http.interfaces.Handler;
import infrastructure.networking.HTTPStatusCodes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class WriteFileRequest extends Handler {
    public WriteFileRequest(Handler next) {
        this.setNext(next);
    }

    @Override
    public Response handle(Request request, Response response) {
        if (response == null) {
            response = new Response(ApplicationConfigs.PROTOCOL, HTTPStatusCodes.CREATED);
        }

        String directory = ApplicationConfigs.getInstance().getFilesDirectory();
        String fileName = request.getRouteParams().get("filename");

        if (directory == null || directory.isBlank()) {
            response.setStatus(HTTPStatusCodes.NOT_FOUND);
            return response;
        }

        var file = new File(directory, fileName);
        try {
            // Canonicalize paths to prevent traversal attacks
            File canonicalBase = new File(directory).getCanonicalFile();
            File canonicalFile = file.getCanonicalFile();
            if (!canonicalFile.toPath().startsWith(canonicalBase.toPath()) || canonicalFile.exists()) {
                response.setStatus(HTTPStatusCodes.NOT_FOUND);
                return response;
            }
        } catch (IOException e) {
            response.setStatus(HTTPStatusCodes.NOT_FOUND);
            return response;
        }

        try {
            if (file.createNewFile()) {
                Files.write(file.toPath(), request.getBody().getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (next != null) {
            return next.handle(request, response);
        }

        return response;
    }
}
