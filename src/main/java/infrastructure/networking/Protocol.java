// Protocol.java
//
// Represents an HTTP protocol version (e.g., HTTP/1.1). Implemented as a
// record for immutability with a custom toString() for response serialization.

package infrastructure.networking;

public record Protocol(String name, String version) {
    @Override
    public String toString() {
        return name + "/" + version;
    }
}
