package infrastructure.networking;

public record Protocol(String name, String version) {
    @Override
    public String toString() {
        return name + "/" + version;
    }
}
