package infrastructure.networking;

public class Protocol {
    private final String name;
    private final String version;

    public Protocol(String name, String version) {
        this.name = name;
        this.version = version;
    }

    @Override
    public String toString() {
        return name + "/" + version;
    }
}
