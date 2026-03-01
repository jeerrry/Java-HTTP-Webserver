// InvalidRequestException.java
//
// Thrown when an incoming HTTP request is malformed or targets an
// unregistered route. Caught by SocketConnectionHandler to return
// a 404 response instead of crashing the connection thread.

package exceptions;

public class InvalidRequestException extends Exception {
    public InvalidRequestException(String message) {
        super(message);
    }
}
