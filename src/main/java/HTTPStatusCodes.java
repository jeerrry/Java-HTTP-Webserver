public class HTTPStatusCodes {
    public record StatusCode(int code, String message) {
        @Override
        public String toString() {
            return code + " " + message;
        }
    }

    public static final StatusCode OK = new StatusCode(200, "OK");
    public static final StatusCode NOTFOUND = new StatusCode(404, "Not Found");
}
