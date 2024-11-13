import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws InvalidRequestException, ParseException {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket;
        var protocol = new Protocol("HTTP", "1.1");
        ApplicationConfigs applicationConfigs = ApplicationConfigs.getInstance(args);

        try {
            serverSocket = new ServerSocket(4221);

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            // Register all server paths here.
            Router
                    .getInstance()
                    .registerRoute("/", request -> new Response(protocol, HTTPStatusCodes.OK, "\r\n\r\n"));
            Router
                    .getInstance()
                    .registerRoute("/echo/{str}", request -> {
                        var response = new Response(protocol, HTTPStatusCodes.OK, "\r\n");

                        String body = request.getRouteParams().get("str");
                        response.addHeader("Content-Type", "text/plain");
                        response.addHeader("Content-Length", body.length() + "");

                        response.addBodyContent(body);

                        return response;
                    });
            Router
                    .getInstance()
                    .registerRoute("/user-agent", request -> {
                        var response = new Response(protocol, HTTPStatusCodes.OK, "\r\n");

                        String body = request.getHeaders().get("User-Agent");
                        response.addHeader("Content-Type", "text/plain");
                        response.addHeader("Content-Length", body.length() + "");

                        response.addBodyContent(body);

                        return response;
                    });

            Router
                    .getInstance()
                    .registerRoute("/files/{filename}", request -> {
                        var response = new Response(protocol, HTTPStatusCodes.OK, "\r\n");
                        String directory = applicationConfigs.getFilesDirectory();
                        String fileName = request.getRouteParams().get("filename");

                        var file = new File(directory, fileName);
                        if (!file.exists()) {
                            response.setStatus(HTTPStatusCodes.NOTFOUND);

                            return response;
                        }

                        response.addHeader("Content-Type", "application/octet-stream");
                        response.addHeader("Content-Length", file.length() + "");

                        return response;
                    });

            while (true) {
                Socket clientConnection = serverSocket.accept(); // Wait for connection from client.
                System.out.println("accepted new connection");
                new Thread(new SocketConnectionHandler(clientConnection, protocol)).start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
