import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Router {
    private static Router instance;
    private final Route route = new Route();
    private final Map<Object, Function<Request, Response>> routes = new HashMap<>();

    public static Router getInstance() {
        if (instance == null) {
            instance = new Router();
        }
        return instance;
    }

    private Router() {
    }

    public void registerRoute(RequestMethod method, String rawPath, Function<Request, Response> handler) throws InvalidRequestException {
        route.patchRouteNodes(rawPath);
        routes.put(method + ":" + rawPath, handler);
    }

    public Response handleRequest(RequestMethod method, Request request) throws InvalidRequestException {
        route.processRequest(request);
        Function<Request, Response> handler = routes.get(method + ":" + request.getPath());
        if (handler == null) {
            throw new InvalidRequestException("Undefined request path: " + request);
        }

        return handler.apply(request);
    }
}
