package infrastructure.routing;

import exceptions.InvalidRequestException;
import http.core.Request;
import http.core.RequestMethod;
import http.core.Response;
import http.interfaces.Handler;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private static final Router instance = new Router();
    private final Route route = new Route();
    private final Map<String, Handler> routes = new HashMap<>();

    public static Router getInstance() {
        return instance;
    }

    private Router() {
    }

    public void registerRoute(RequestMethod method, String rawPath, Handler requestHandler) throws InvalidRequestException {
        route.patchRouteNodes(method, rawPath);
        routes.put(method + ":" + rawPath, requestHandler);
    }

    public Response handleRequest(Request request) throws InvalidRequestException {
        route.processRequest(request);
        Handler handler = routes.get(request.getRequestMethod() + ":" + request.getPath());
        if (handler == null) {
            throw new InvalidRequestException("Undefined request path: " + request.getPath());
        }

        return handler.handle(request, null);
    }
}
