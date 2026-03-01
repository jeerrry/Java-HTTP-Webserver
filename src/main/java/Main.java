// Main.java
//
// Server entry point. Registers HTTP routes with their handler chains,
// starts a virtual-thread-backed server socket, and dispatches each
// accepted connection to a SocketConnectionHandler.

import configuration.ApplicationConfigs;
import exceptions.InvalidRequestException;
import http.core.RequestMethod;
import http.handler.*;
import http.middleware.EncodingHeaderReaderMiddleware;
import http.middleware.GZIPEncoderMiddleware;
import infrastructure.networking.SocketConnectionHandler;
import infrastructure.routing.Router;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int SERVER_PORT = 4221;

    public static void main(String[] args) throws InvalidRequestException, ParseException {
        ApplicationConfigs.init(args);

        Router router = Router.getInstance();
        router.registerRoute(RequestMethod.GET, "/", new RootRequest(null));
        router.registerRoute(RequestMethod.GET, "/echo/{str}",
                new EncodingHeaderReaderMiddleware(new EchoRequest(new GZIPEncoderMiddleware(null))));
        router.registerRoute(RequestMethod.GET, "/user-agent", new UserAgentRequest(null));
        router.registerRoute(RequestMethod.GET, "/files/{filename}", new ReadFileRequest(null));
        router.registerRoute(RequestMethod.POST, "/files/{filename}", new WriteFileRequest(null));

        // Virtual threads handle blocking I/O without tying up platform threads
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            serverSocket.setReuseAddress(true);
            System.out.println("Server started on port " + SERVER_PORT);
            while (true) {
                var clientConnection = serverSocket.accept();
                executor.submit(new SocketConnectionHandler(clientConnection));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
