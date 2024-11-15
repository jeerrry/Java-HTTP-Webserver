package http.interfaces;

import http.core.Request;
import http.core.Response;

public abstract class Handler {
    protected Handler next;

    public void setNext(Handler next) {
        this.next = next;
    }

    public abstract Response handle(Request request, Response response);
}
