import java.util.HashMap;
import java.util.Map;

public class Request {
    private final Protocol protocol;
    private final RequestMethod requestMethod;
    private final String target;
    private final Map<String, String> headers;
    private final Map<String, String> bodyParams;

    public Request(Protocol protocol, RequestMethod requestMethod, String target) {
        headers = new HashMap<>();
        bodyParams = new HashMap<>();

        this.protocol = protocol;
        this.requestMethod = requestMethod;
        this.target = target;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value + "\r\n");
    }

    public String getTarget() {
        return target;
    }
}
