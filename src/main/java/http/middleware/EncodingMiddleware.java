package http.middleware;

import configuration.ApplicationConfigs;
import http.core.Request;
import http.core.Response;
import http.interfaces.Handler;
import infrastructure.networking.HTTPStatusCodes;

public class EncodingMiddleware extends Handler {
    public EncodingMiddleware(Handler next) {
        this.next = next;
    }

    @Override
    public Response handle(Request request, Response response) {
        if (response == null) {
            response = new Response(ApplicationConfigs.PROTOCOL, HTTPStatusCodes.OK);
        }

        if (request.getHeaders().containsKey("Accept-Encoding")) {
            String value = request.getHeaders().get("Accept-Encoding");
            if (value.equals("gzip")) {
                response.addHeader("Content-Encoding", "gzip");
            }
        }

        if (next != null) {
            return next.handle(request, response);
        }

        return response;
    }
}
