import java.util.HashMap;
import java.util.Map;

public class Request {
    private final Protocol protocol;
    private final RequestMethod requestMethod;
    private String path;
    private final Map<String, String> headers;
    private final Map<String, String> routeParams;
    private String body;

    public Request(Protocol protocol, RequestMethod requestMethod, String path) {
        headers = new HashMap<>();
        routeParams = new HashMap<>();
        body = "";

        this.protocol = protocol;
        this.requestMethod = requestMethod;
        this.path = path;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addPathParam(String key, String value) {
        routeParams.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getRouteParams() {
        return routeParams;
    }

    public String getBody() {
        return body;
    }

    public String getPath() {
        return path;
    }

    public void updatePath(String newPath) {
        path = newPath;
    }
}
