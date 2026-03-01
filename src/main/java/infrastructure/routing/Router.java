// Router.java
//
// Central request dispatcher. Maps resolved route paths to their handler
// chains using an eager-initialized singleton. The Route trie handles URL
// matching while this class owns the path-to-handler mapping.

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

    /** Registers a handler chain for the given method and path template. */
    public void registerRoute(RequestMethod method, String rawPath, Handler handler) throws InvalidRequestException {
        route.registerRoute(method, rawPath);
        routes.put(method + ":" + rawPath, handler);
    }

    /**
     * Resolves the request path against the route trie and dispatches
     * to the matching handler chain.
     */
    public Response handleRequest(Request request) throws InvalidRequestException {
        route.resolveRequest(request);
        Handler handler = routes.get(request.getRequestMethod() + ":" + request.getPath());
        if (handler == null) {
            throw new InvalidRequestException("Undefined request path: " + request.getPath());
        }
        return handler.handle(request, null);
    }
}
