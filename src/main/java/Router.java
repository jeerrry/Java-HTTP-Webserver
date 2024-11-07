import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Router {
    private static Router instance;
    private final Route route = new Route();
    private final Map<String, Function<Request, Response>> routes = new HashMap<>();

    public static Router getInstance() {
        if (instance == null) {
            instance = new Router();
        }
        return instance;
    }

    private Router() {
    }

    public void registerRoute(String rawPath, Function<Request, Response> handler) throws InvalidRequestException {
        route.patchRouteNodes(rawPath);
        routes.put(rawPath, handler);
    }

    public Response handleRequest(Request request) throws InvalidRequestException {
        route.processRequest(request);
        Function<Request, Response> handler = routes.get(request.getPath());
        if (handler == null) {
            throw new InvalidRequestException("Undefined request path: " + request);
        }

        return handler.apply(request);
    }
}
