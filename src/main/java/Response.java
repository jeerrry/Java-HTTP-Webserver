import java.util.HashMap;
import java.util.Map;

public class Response {
    private final Protocol protocol;
    private final StringBuilder body;
    private StringBuilder builder;
    private final String sectionSeparator;
    private final Map<String, String> headers;

    public Response(Protocol protocol, HTTPStatusCodes.StatusCode statusCode, String sectionSeparator) {
        builder = new StringBuilder();
        body = new StringBuilder();
        this.protocol = protocol;
        this.sectionSeparator = sectionSeparator;
        headers = new HashMap<>();

        setStatus(statusCode);
    }

    public void setStatus(HTTPStatusCodes.StatusCode statusCode) {
        builder = new StringBuilder();
        builder.append(protocol.toString()).append(" ").append(statusCode.toString()).append(sectionSeparator);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    private String buildHeaders() {
        StringBuilder result = new StringBuilder();
        if (headers.isEmpty()) return "";

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            result.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        return result + "\r\n";
    }

    public void addBodyContent(String content) {
        body.append(content);
    }

    @Override
    public String toString() {
        return builder.toString() + buildHeaders() + body.toString();
    }
}
