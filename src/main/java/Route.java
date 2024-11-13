import java.util.*;

public class Route {
    private String value = "/";
    private String fullPath;
    private boolean isComplete;
    private Map<String, Route> paths = new HashMap<>();

    public void patchRouteNodes(String rawPath) throws InvalidRequestException {
        Route current = this;
        String[] fullPath = rawPath.split("/");
        for (int i = 0; i < fullPath.length; i++) {
            String path = fullPath[i];
            if (path.isBlank()) continue;

            boolean isDynamic = path.startsWith("{") && path.endsWith("}");
            path = isDynamic ? "{*}" : path;
            if (!current.paths.containsKey(path)) {
                var route = new Route();
                route.value = path;
                current.paths.put(path, route);
            }
            current = current.paths.get(path);
        }

        if (current.isComplete) {
            throw new InvalidRequestException("Provided path already exists");
        }

        current.isComplete = true;
        current.fullPath = rawPath;
    }

    public void processRequest(Request request) throws InvalidRequestException {
        Route current = this;
        String[] fullPath = request.getPath().split("/");
        var routeParamValues = new ArrayList<String>();
        boolean validPath = true;
        String currentFullPath = "/";

        for (String path : fullPath) {
            if (path.isBlank()) continue;

            if (current.paths.containsKey(path)) {
                current = current.paths.get(path);
                currentFullPath = current.fullPath;
                validPath = current.isComplete;
                continue;
            }

            if (current.paths.containsKey("{*}")) {
                current = current.paths.get("{*}");
                validPath = current.isComplete;
                currentFullPath = current.fullPath;
                routeParamValues.add(path);
                continue;
            }

            throw new InvalidRequestException("Provided path does not exist");
        }

        if (!validPath) {
            throw new InvalidRequestException("Provided path does not exist");
        }


        List<String> routeParamNames = currentFullPath != null ? Arrays.stream(currentFullPath.split("/")).filter(x -> x.startsWith("{") && x.endsWith("}")).toList() : new ArrayList<>();
        for (int i = 0; i < routeParamNames.size(); i++) {
            String param = routeParamNames.get(i).substring(1, routeParamNames.get(i).length() - 1);
            String value = routeParamValues.get(i);

            request.addPathParam(param, value);
        }

        request.updatePath(currentFullPath);
    }
}
