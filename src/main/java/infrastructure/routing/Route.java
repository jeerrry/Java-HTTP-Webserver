// Route.java
//
// Trie-based URL router. Each node represents a path segment, with "{*}"
// nodes matching dynamic parameters. Resolves incoming request paths against
// registered routes and extracts named path parameters.
//
// Example: registering "/echo/{str}" creates nodes: root -> "echo" -> "{*}"
// A request to "/echo/hello" resolves to that route with str="hello".

package infrastructure.routing;

import exceptions.InvalidRequestException;
import http.core.Request;
import http.core.RequestMethod;

import java.util.*;

public class Route {
    private String segment = "/";
    private String fullPath;
    private boolean complete;
    private final Set<RequestMethod> methods = new HashSet<>();
    private final Map<String, Route> children = new HashMap<>();

    /**
     * Inserts a route into the trie. Dynamic path segments like "{str}" are
     * stored under the wildcard key "{*}" to enable single-lookup matching.
     */
    public void registerRoute(RequestMethod method, String rawPath) throws InvalidRequestException {
        Route current = this;
        String[] segments = rawPath.split("/");

        for (String seg : segments) {
            if (seg.isBlank()) continue;

            boolean isDynamic = seg.startsWith("{") && seg.endsWith("}");
            String key = isDynamic ? "{*}" : seg;

            if (!current.children.containsKey(key)) {
                var route = new Route();
                route.segment = key;
                current.children.put(key, route);
            }
            current = current.children.get(key);
        }

        if (current.complete && current.methods.contains(method)) {
            throw new InvalidRequestException("Provided path already exists");
        }

        current.complete = true;
        current.fullPath = rawPath;
        current.methods.add(method);
    }

    /**
     * Resolves a request path against the trie, extracts dynamic parameters,
     * and updates the request with the matched route template path.
     */
    public void resolveRequest(Request request) throws InvalidRequestException {
        Route current = this;
        String[] segments = request.getPath().split("/");
        var routeParamValues = new ArrayList<String>();
        boolean validPath = true;
        String matchedPath = "/";

        for (String seg : segments) {
            if (seg.isBlank()) continue;

            if (current.children.containsKey(seg)) {
                current = current.children.get(seg);
                matchedPath = current.fullPath;
                validPath = current.complete && current.methods.contains(request.getRequestMethod());
                continue;
            }

            if (current.children.containsKey("{*}")) {
                current = current.children.get("{*}");
                validPath = current.complete && current.methods.contains(request.getRequestMethod());
                matchedPath = current.fullPath;
                routeParamValues.add(seg);
                continue;
            }

            throw new InvalidRequestException("Provided path does not exist");
        }

        if (!validPath) {
            throw new InvalidRequestException("Provided path does not exist");
        }

        // Extract named parameters from the route template and pair them with captured values
        List<String> routeParamNames = matchedPath != null
                ? Arrays.stream(matchedPath.split("/"))
                    .filter(x -> x.startsWith("{") && x.endsWith("}"))
                    .toList()
                : new ArrayList<>();

        for (int i = 0; i < routeParamNames.size() && i < routeParamValues.size(); i++) {
            String param = routeParamNames.get(i).substring(1, routeParamNames.get(i).length() - 1);
            request.addPathParam(param, routeParamValues.get(i));
        }

        request.setPath(matchedPath);
    }
}
