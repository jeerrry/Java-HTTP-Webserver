package http.handler;

import configuration.ApplicationConfigs;
import http.core.Request;
import http.core.Response;
import http.interfaces.Handler;
import infrastructure.networking.HTTPStatusCodes;

public class RootRequest extends Handler {
    public RootRequest(Handler handler) {
        setNext(handler);
    }

    @Override
    public Response handle(Request request, Response response) {
        response = new Response(ApplicationConfigs.PROTOCOL, HTTPStatusCodes.OK);
        if (next != null) {
            return next.handle(request, response);
        }

        return response;
    }
}
