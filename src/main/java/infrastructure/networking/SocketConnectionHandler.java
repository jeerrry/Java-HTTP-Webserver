package infrastructure.networking;

import configuration.ApplicationConfigs;
import exceptions.InvalidRequestException;
import http.core.Request;
import http.core.RequestFactory;
import http.core.Response;
import infrastructure.routing.Router;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketConnectionHandler implements Runnable {
    private final Socket socket;
    private final byte[] buffer;

    public SocketConnectionHandler(Socket socket) {
        this.socket = socket;
        buffer = new byte[1024];
    }

    @Override
    public void run() {
        try {
            InputStream stream = socket.getInputStream();
            int size = stream.read(buffer);
            String request = new String(buffer, 0, size);

            var writer = new PrintWriter(socket.getOutputStream());
            var response = new Response(ApplicationConfigs.PROTOCOL, HTTPStatusCodes.OK);
            try {
                Request httpRequest = RequestFactory.getRequest(request);
                response = Router.getInstance().handleRequest(httpRequest);
            } catch (InvalidRequestException e) {
                response.setStatus(HTTPStatusCodes.NOTFOUND);
                throw new RuntimeException(e);
            } finally {
                writer.println(response);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
