public class RequestFactory {
    public static Request getRequest(String request) throws InvalidRequestException {
        String[] parts = request.split("\r\n");

        // Process request line
        String[] requestParts = parts[0].split(" ");
        RequestMethod method = RequestMethod.contains(requestParts[0]);
        if (method == RequestMethod.UNDEFINED) {
            throw new InvalidRequestException("Undefined request: " + request);
        }

        String path = requestParts[1];
        if (path.isBlank()) {
            throw new InvalidRequestException("Empty path: " + path);
        }

        String[] protocol = requestParts[2].split("/");

        return new Request(new Protocol(protocol[0], protocol[1]), method, path);
    }
}
