// SocketConnectionHandler.java
//
// Handles a single HTTP connection on a virtual thread. Reads the raw
// request (headers line-by-line, then Content-Length bytes for body),
// dispatches to the router, and writes the serialized response.
// The socket is closed via try-with-resources to prevent file descriptor leaks.

package infrastructure.networking;

import configuration.ApplicationConfigs;
import exceptions.InvalidRequestException;
import http.core.Request;
import http.core.RequestFactory;
import http.core.Response;
import infrastructure.routing.Router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketConnectionHandler implements Runnable {
    private static final String CONTENT_LENGTH_PREFIX = "content-length:";
    private final Socket socket;

    public SocketConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (socket; OutputStream out = socket.getOutputStream()) {
            String rawRequest = readRawRequest();
            Response response;
            try {
                Request httpRequest = RequestFactory.getRequest(rawRequest);
                response = Router.getInstance().handleRequest(httpRequest);
            } catch (InvalidRequestException e) {
                response = new Response(ApplicationConfigs.PROTOCOL, HTTPStatusCodes.NOT_FOUND);
            }
            out.write(response.toBytes());
            out.flush();
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error handling request: " + e.getMessage());
        }
    }

    /**
     * Reads the full HTTP request: headers line-by-line until the blank line,
     * then exactly Content-Length bytes for the body.
     */
    private String readRawRequest() throws IOException {
        var reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        var headerBuilder = new StringBuilder();
        String line;
        int contentLength = 0;

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) break;
            headerBuilder.append(line).append("\r\n");
            if (line.toLowerCase().startsWith(CONTENT_LENGTH_PREFIX)) {
                contentLength = Integer.parseInt(
                        line.substring(CONTENT_LENGTH_PREFIX.length()).trim());
            }
        }

        var body = new StringBuilder();
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            int totalRead = 0;
            while (totalRead < contentLength) {
                int read = reader.read(buffer, totalRead, contentLength - totalRead);
                if (read == -1) break;
                totalRead += read;
            }
            body.append(buffer, 0, totalRead);
        }

        return headerBuilder + "\r\n" + body;
    }
}
