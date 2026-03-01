package infrastructure.networking;

public class HTTPStatusCodes {
    public record StatusCode(int code, String message) {
        @Override
        public String toString() {
            return code + " " + message;
        }
    }

    public static final StatusCode OK = new StatusCode(200, "OK");
    public static final StatusCode CREATED = new StatusCode(201, "Created");
    public static final StatusCode BAD_REQUEST = new StatusCode(400, "Bad Request");
    public static final StatusCode NOT_FOUND = new StatusCode(404, "Not Found");
    public static final StatusCode INTERNAL_SERVER_ERROR = new StatusCode(500, "Internal Server Error");
}
