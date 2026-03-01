// RequestFactory.java
//
// Parses raw HTTP/1.1 request strings into Request objects. Handles the
// request line, headers, and body as defined in RFC 7230. Header parsing
// uses indexOf(':') to correctly handle colons in header values (e.g.,
// Host, Authorization, URLs).

package http.core;

import exceptions.InvalidRequestException;
import infrastructure.networking.Protocol;

public class RequestFactory {

    /** Parses a raw HTTP request string into a structured Request object. */
    public static Request getRequest(String request) throws InvalidRequestException {
        String[] majorParts = request.split("\r\n\r\n", 2);
        String[] headerLines = majorParts[0].split("\r\n");

        if (headerLines.length == 0 || headerLines[0].isBlank()) {
            throw new InvalidRequestException("Empty request");
        }

        Request req = parseRequestLine(headerLines[0]);
        for (int i = 1; i < headerLines.length; i++) {
            parseHeader(req, headerLines[i]);
        }
        if (majorParts.length > 1 && !majorParts[1].isEmpty()) {
            req.setBody(majorParts[1]);
        }

        return req;
    }

    private static Request parseRequestLine(String line) throws InvalidRequestException {
        String[] parts = line.split(" ");
        if (parts.length < 3) {
            throw new InvalidRequestException("Malformed request line: " + line);
        }

        RequestMethod method = RequestMethod.fromString(parts[0]);
        if (method == RequestMethod.UNDEFINED) {
            throw new InvalidRequestException("Undefined method: " + parts[0]);
        }

        String path = parts[1];
        if (path.isBlank()) {
            throw new InvalidRequestException("Empty path");
        }

        String[] protocol = parts[2].split("/");
        if (protocol.length < 2) {
            throw new InvalidRequestException("Malformed protocol: " + parts[2]);
        }

        return new Request(new Protocol(protocol[0], protocol[1]), method, path);
    }

    private static void parseHeader(Request context, String headerLine) {
        int colonIndex = headerLine.indexOf(':');
        if (colonIndex < 0) return;
        String key = headerLine.substring(0, colonIndex).trim();
        String value = headerLine.substring(colonIndex + 1).trim();
        if (!key.isEmpty()) {
            context.addHeader(key, value);
        }
    }
}
