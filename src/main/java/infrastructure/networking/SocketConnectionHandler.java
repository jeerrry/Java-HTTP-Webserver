// SocketConnectionHandler.java
//
// Handles a persistent HTTP/1.1 connection on a virtual thread. Loops
// reading requests until the client sends Connection: close, the socket
// times out (idle connection), or the client closes the connection (EOF).
// The BufferedReader is created once and reused across requests to preserve
// its internal buffer between sequential reads.

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
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SocketConnectionHandler implements Runnable {
    private static final String CONTENT_LENGTH_PREFIX = "content-length:";
    private static final int IDLE_TIMEOUT_MS = 5000;
    private final Socket socket;

    public SocketConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (socket;
             var reader = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             OutputStream out = socket.getOutputStream()) {

            socket.setSoTimeout(IDLE_TIMEOUT_MS);
            boolean keepAlive = true;

            while (keepAlive) {
                String rawRequest;
                try {
                    rawRequest = readRawRequest(reader);
                } catch (SocketTimeoutException e) {
                    break;
                }

                if (rawRequest == null) {
                    break;
                }

                Response response;
                try {
                    Request httpRequest = RequestFactory.getRequest(rawRequest);
                    keepAlive = !isConnectionClose(httpRequest);
                    response = Router.getInstance().handleRequest(httpRequest);
                } catch (InvalidRequestException e) {
                    response = new Response(ApplicationConfigs.PROTOCOL, HTTPStatusCodes.NOT_FOUND);
                    keepAlive = false;
                }

                if (!keepAlive) {
                    response.addHeader("Connection", "close");
                }

                out.write(response.toBytes());
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error handling request: " + e.getMessage());
        }
    }

    /** Checks if the request's Connection header signals closure (case-insensitive). */
    private boolean isConnectionClose(Request request) {
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Connection")) {
                return entry.getValue().equalsIgnoreCase("close");
            }
        }
        return false;
    }

    /**
     * Reads the full HTTP request from a persistent connection. Returns null
     * on EOF (client closed connection). The reader is shared across requests
     * on the same connection to preserve buffered data.
     */
    private String readRawRequest(BufferedReader reader) throws IOException {
        var headerBuilder = new StringBuilder();
        String line;
        int contentLength = 0;

        // First readLine detects EOF (client closed connection)
        line = reader.readLine();
        if (line == null) return null;

        headerBuilder.append(line).append("\r\n");

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
