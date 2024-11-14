import java.util.Arrays;
import java.util.stream.Collectors;

public class RequestFactory {
    public static Request getRequest(String request) throws InvalidRequestException {
        String[] majorParts = request.split("\r\n\r\n");
        String[] upperPats = majorParts[0].split("\r\n");

        Request req = getStatusLine(upperPats[0]);
        processHeaders(req, Arrays.stream(upperPats, 1, upperPats.length).collect(Collectors.joining("\r\n")));
        processBody(req, majorParts.length > 1 ? majorParts[1] : "");

        return req;
    }

    private static Request getStatusLine(String statusLine) throws InvalidRequestException {
        // Process request line
        String[] requestParts = statusLine.split(" ");
        RequestMethod method = RequestMethod.contains(requestParts[0]);
        if (method == RequestMethod.UNDEFINED) {
            throw new InvalidRequestException("Undefined request");
        }

        String path = requestParts[1];
        if (path.isBlank()) {
            throw new InvalidRequestException("Empty path: " + path);
        }

        String[] protocol = requestParts[2].split("/");

        return new Request(new Protocol(protocol[0], protocol[1]), method, path);
    }

    private static void processHeaders(Request context, String rawHeader) throws InvalidRequestException {
        if (rawHeader.isBlank()) return;

        String[] headers = rawHeader.split("\r\n");
        for (String header : headers) {
            header = header.replaceAll("\r\n", "");
            String[] parts = header.split(":");
            context.addHeader(parts[0].trim(), parts[1].trim());
        }
    }

    private static void processBody(Request context, String rawBody) throws InvalidRequestException {
        if (rawBody.isBlank()) return;

        context.setBody(rawBody);
    }
}
