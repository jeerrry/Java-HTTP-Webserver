package http.middleware;

import configuration.ApplicationConfigs;
import http.core.Request;
import http.core.Response;
import http.interfaces.Handler;
import infrastructure.networking.HTTPStatusCodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class GZIPEncoderMiddleware extends Handler {
    public GZIPEncoderMiddleware(Handler next) {
        this.setNext(next);
    }

    @Override
    public Response handle(Request request, Response response) {
        if (response == null) {
            response = new Response(ApplicationConfigs.PROTOCOL, HTTPStatusCodes.NOTFOUND);
        }

        if (!response.getHeaders().containsKey("Content-Encoding")) {
            return response;
        }

        if (!response.getHeaders().get("Content-Encoding").equals("gzip")) {
            return response;
        }

        var body = response.getBody();
        var byteArrayOutputStream = new ByteArrayOutputStream();
        try(var gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(body.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var gzipData = byteArrayOutputStream.toByteArray();
        response.addHeader("Content-Length", gzipData.length + "");
        response.clearBody();
        response.setBodyBytes(gzipData);

        if (next != null) {
            return next.handle(request, response);
        }

        return response;
    }
}
