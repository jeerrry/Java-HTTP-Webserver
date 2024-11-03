public class Response {
    private final Protocol protocol;
    private StringBuilder builder;
    private final String sectionSeparator;

    public Response(Protocol protocol, HTTPStatusCodes.StatusCode statusCode, String sectionSeparator) {
        builder = new StringBuilder();
        this.protocol = protocol;
        this.sectionSeparator = sectionSeparator;

        setStatus(statusCode);
    }

    public void setStatus(HTTPStatusCodes.StatusCode statusCode) {
        builder = new StringBuilder();
        builder.append(protocol.toString()).append(" ").append(statusCode.toString()).append(sectionSeparator);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
