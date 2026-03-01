// Handler.java
//
// Base of the Chain of Responsibility pattern for request processing.
// Each handler in the chain can process the request and optionally
// delegate to the next handler. Middleware (encoding, compression)
// and endpoint handlers (echo, file) both extend this class.
//
// Lives in http.interfaces despite being an abstract class (not an
// interface) because it defines the contract for all request handlers.

package http.interfaces;

import http.core.Request;
import http.core.Response;

public abstract class Handler {
    protected Handler next;

    public void setNext(Handler next) {
        this.next = next;
    }

    /** Processes the request and returns a response, optionally delegating to the next handler. */
    public abstract Response handle(Request request, Response response);
}
