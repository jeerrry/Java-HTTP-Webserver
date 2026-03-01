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

    public static void main(String[] args) throws InvalidRequestException, ParseException {
        ApplicationConfigs.init(args);

        Router.getInstance()
                .registerRoute(RequestMethod.GET, "/", new RootRequest(null));
        Router.getInstance()
                .registerRoute(RequestMethod.GET, "/echo/{str}", new EncodingHeaderReaderMiddleware(new EchoRequest(new GZIPEncoderMiddleware(null))));
        Router.getInstance()
                .registerRoute(RequestMethod.GET, "/user-agent", new UserAgentRequest(null));
        Router.getInstance()
                .registerRoute(RequestMethod.GET, "/files/{filename}", new ReadFileRequest(null));
        Router.getInstance()
                .registerRoute(RequestMethod.POST, "/files/{filename}", new WriteFileRequest(null));

        try (ServerSocket serverSocket = new ServerSocket(4221);
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            serverSocket.setReuseAddress(true);
            System.out.println("Server started on port 4221");
            while (true) {
                var clientConnection = serverSocket.accept();
                executor.submit(new SocketConnectionHandler(clientConnection));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
