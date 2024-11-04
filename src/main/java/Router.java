import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Router {
    private Map<String, Function<Request, Response>> routes = new HashMap<>();
    private static Router instance;

    public static Router getInstance() {
        if (instance == null) {
            instance = new Router();
        }
        return instance;
    }

    private Router() {
    }

    public void registerRoute(String path, Function<Request, Response> handler) {
        routes.put(path, handler);
    }

    public Response handleRequest(Request request) throws InvalidRequestException {
        Function<Request, Response> handler = routes.get(request.getPath());
        if (handler == null) {
            throw new InvalidRequestException("Undefined request path: " + request);
        }

        return handler.apply(request);
    }
}
