// Response.java
//
// Represents an HTTP response. Supports both text and binary body content
// for cases like GZIP compression where the body is raw bytes. The toBytes()
// method serializes the complete response into a single byte array for
// efficient socket writes.

package http.core;

import infrastructure.networking.HTTPStatusCodes;
import infrastructure.networking.Protocol;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Response {
    private final Protocol protocol;
    private final StringBuilder body;
    private StringBuilder statusLine;
    private final Map<String, String> headers;
    private byte[] bodyBytes;

    public Response(Protocol protocol, HTTPStatusCodes.StatusCode statusCode) {
        this.protocol = protocol;
        statusLine = new StringBuilder();
        body = new StringBuilder();
        headers = new LinkedHashMap<>();
        bodyBytes = new byte[0];

        setStatus(statusCode);
    }

    public void setStatus(HTTPStatusCodes.StatusCode statusCode) {
        statusLine = new StringBuilder();
        statusLine.append(protocol.toString()).append(" ").append(statusCode.toString()).append("\r\n");
    }

    public void clearHeaders() {
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

    /**
     * Serializes the complete HTTP response (status line, headers, body) into bytes.
     * Uses bodyBytes if set (binary/compressed), otherwise uses the text body.
     */
    public byte[] toBytes() {
        byte[] headerBytes = (statusLine.toString() + buildHeaders()).getBytes(StandardCharsets.UTF_8);
        byte[] content = bodyBytes.length > 0 ? bodyBytes : body.toString().getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[headerBytes.length + content.length];
        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(content, 0, result, headerBytes.length, content.length);
        return result;
    }

    @Override
    public String toString() {
        return statusLine.toString() + buildHeaders() + body.toString();
    }
}
