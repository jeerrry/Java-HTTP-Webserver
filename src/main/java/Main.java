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
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws InvalidRequestException, ParseException {
        ServerSocket serverSocket;
        ApplicationConfigs.init(args);

        Router
                .getInstance()
                .registerRoute(RequestMethod.GET, "/", new RootRequest(null));
        Router
                .getInstance()
                .registerRoute(RequestMethod.GET, "/echo/{str}", new EncodingHeaderReaderMiddleware(new EchoRequest(new GZIPEncoderMiddleware(null))));
        Router
                .getInstance()
                .registerRoute(RequestMethod.GET, "/user-agent", new UserAgentRequest(null));
        Router
                .getInstance()
                .registerRoute(RequestMethod.GET, "/files/{filename}", new ReadFileRequest(null));
        Router
                .getInstance()
                .registerRoute(RequestMethod.POST, "/files/{filename}", new WriteFileRequest(null));

        try {
            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);
            while (true) {
                Socket clientConnection = serverSocket.accept(); // Wait for connection from client.
                System.out.println("accepted new connection");
                new Thread(new SocketConnectionHandler(clientConnection)).start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
