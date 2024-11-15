package http.handler;

import configuration.ApplicationConfigs;
import http.core.Request;
import http.core.Response;
import http.interfaces.Handler;
import infrastructure.networking.HTTPStatusCodes;

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
            response.cleanHeaders();
            response.clearBody();
            response.setStatus(HTTPStatusCodes.NOTFOUND);

            return response;
        }

        response.addHeader("Content-Type", "text/plain");
        response.addHeader("Content-Length", body.length() + "");

        response.addBodyContent(body);

        if (next != null) {
            return next.handle(request, response);
        }

        return response;
    }
}
