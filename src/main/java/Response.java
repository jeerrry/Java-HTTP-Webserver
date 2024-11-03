public class Response {
    private final Protocol protocol;
    private final StringBuilder builder;
    private final String sectionSeperator;

    public Response(Protocol protocol, String sectionSeparator) {
        builder = new StringBuilder();
        this.protocol = protocol;
        this.sectionSeperator = sectionSeparator;
    }

    public void addHeader(String statusCode) {
        builder.append(protocol.toString()).append(" ").append(statusCode).append(sectionSeperator);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
