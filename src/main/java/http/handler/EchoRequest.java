// EchoRequest.java
//
// Echoes the captured path parameter "{str}" back as the response body.
// Used by the /echo/{str} route. Works with the encoding middleware chain
// for gzip compression support.

package http.handler;

import configuration.ApplicationConfigs;
import http.core.Request;
import http.core.Response;
import http.interfaces.Handler;
import infrastructure.networking.HTTPStatusCodes;

import java.nio.charset.StandardCharsets;

public class EchoRequest extends Handler {
    public EchoRequest(Handler next) {
        this.setNext(next);
    }

    @Override
    public Response handle(Request request, Response response) {
        if (response == null) {
            response = new Response(ApplicationConfigs.PROTOCOL, HTTPStatusCodes.OK);
        }

        String body = request.getRouteParams().get("str");
        if (body == null) {
            response.clearHeaders();
            response.clearBody();
            response.setStatus(HTTPStatusCodes.NOT_FOUND);
            return response;
        }

        response.addHeader("Content-Type", "text/plain");
        response.addHeader("Content-Length", body.getBytes(StandardCharsets.UTF_8).length + "");

        response.addBodyContent(body);

        if (next != null) {
            return next.handle(request, response);
        }

        return response;
    }
}
