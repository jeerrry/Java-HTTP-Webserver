package http.core;

import infrastructure.networking.HTTPStatusCodes;
import infrastructure.networking.Protocol;

import java.util.LinkedHashMap;
import java.util.Map;

public class Response {
    private final Protocol protocol;
    private final StringBuilder body;
    private StringBuilder builder;
    private final Map<String, String> headers;
    private byte[] bodyBytes;

    public Response(Protocol protocol, HTTPStatusCodes.StatusCode statusCode) {
        builder = new StringBuilder();
        body = new StringBuilder();
        this.protocol = protocol;
        headers = new LinkedHashMap<>();
        bodyBytes = new byte[0];

        setStatus(statusCode);
    }

    public void setStatus(HTTPStatusCodes.StatusCode statusCode) {
        builder = new StringBuilder();
        builder.append(protocol.toString()).append(" ").append(statusCode.toString()).append("\r\n");
    }

    public void cleanHeaders() {
        headers.clear();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    private String buildHeaders() {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            result.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        return result + "\r\n";
    }

    public void clearBody() {
        body.delete(0, body.length());
    }

    public void addBodyContent(String content) {
        body.append(content);
    }

    public String getBody() {
        return body.toString();
    }

    public void setBodyBytes(byte[] bodyBytes) {
        this.bodyBytes = bodyBytes;
    }

    public byte[] getBodyBytes() {
        return bodyBytes;
    }

    @Override
    public String toString() {
        return builder.toString() + buildHeaders() + body.toString();
    }
}
