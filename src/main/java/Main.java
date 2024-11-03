import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

public class Main {
    private static final byte[] buffer = new byte[1024];

    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket;
        var paths = new HashSet<String>(List.of("/"));
        var protocol = new Protocol("HTTP", "1.1");

        try {
            serverSocket = new ServerSocket(4221);

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            Socket rawRequest = serverSocket.accept(); // Wait for connection from client.
            InputStream stream = rawRequest.getInputStream();
            int size = stream.read(buffer);
            String request = new String(buffer, 0, size);

            var writer = new PrintWriter(rawRequest.getOutputStream());
            var response = new Response(protocol, HTTPStatusCodes.OK, "\r\n\r\n");
            try {
                var httpRequest = RequestFactory.getRequest(request);
                if (paths.contains(httpRequest.getTarget())) {
                    response.setStatus(HTTPStatusCodes.OK);
                }else {
                    response.setStatus(HTTPStatusCodes.NOTFOUND);
                }
            } catch (InvalidRequestException e) {
                throw new RuntimeException(e);
            } finally {
                writer.println(response);
                writer.flush();
                writer.close();
            }
            System.out.println("accepted new connection");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
