// RequestMethod.java
//
// HTTP method enumeration. Provides safe parsing from raw strings,
// returning UNDEFINED for unrecognized methods rather than throwing.

package http.core;

public enum RequestMethod {
    UNDEFINED, GET, POST, PUT, DELETE;

    /**
     * Parses a raw HTTP method string into the corresponding enum value.
     * Returns UNDEFINED for unrecognized methods, avoiding exception overhead
     * in the hot path of request parsing.
     */
    public static RequestMethod fromString(String method) {
        for (RequestMethod m : RequestMethod.values()) {
            if (m.toString().equalsIgnoreCase(method)) {
                return m;
            }
        }
        return UNDEFINED;
    }
}
