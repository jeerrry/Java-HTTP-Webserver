package http.handler;

import configuration.ApplicationConfigs;
import http.core.Request;
import http.core.Response;
import http.interfaces.Handler;
import infrastructure.networking.HTTPStatusCodes;

public class UserAgentRequest extends Handler {
    public UserAgentRequest(Handler next) {
        this.setNext(next);
    }

    @Override
    public Response handle(Request request, Response response) {
        if (response == null) {
            response = new Response(ApplicationConfigs.PROTOCOL, HTTPStatusCodes.OK);
        }

        String body = request.getHeaders().get("User-Agent");
        response.addHeader("Content-Type", "text/plain");
        response.addHeader("Content-Length", body.length() + "");

        response.addBodyContent(body);

        if (next != null) {
            return next.handle(request, response);
        }

        return response;
    }
}
