// EncodingHeaderReaderMiddleware.java
//
// Reads the Accept-Encoding header and sets Content-Encoding on the response
// if a supported compression scheme is found. Rejects values containing
// CR/LF characters to prevent HTTP response splitting.

package http.middleware;

import configuration.ApplicationConfigs;
import http.core.Request;
import http.core.Response;
import http.interfaces.Handler;
import infrastructure.networking.HTTPStatusCodes;

public class EncodingHeaderReaderMiddleware extends Handler {
    public EncodingHeaderReaderMiddleware(Handler next) {
        this.setNext(next);
    }

    @Override
    public Response handle(Request request, Response response) {
        if (response == null) {
            response = new Response(ApplicationConfigs.PROTOCOL, HTTPStatusCodes.OK);
        }

        if (request.getHeaders().containsKey("Accept-Encoding")) {
            String value = request.getHeaders().get("Accept-Encoding");

            if (!value.isBlank()) {
                String[] values = value.split(",");
                for (String encoding : values) {
                    if (encoding.isBlank()) continue;
                    String trimmed = encoding.trim();
                    if (trimmed.contains("\r") || trimmed.contains("\n")) continue;
                    if (ApplicationConfigs.SUPPORTED_COMPRESSIONS.contains(trimmed)) {
                        response.addHeader("Content-Encoding", trimmed);
                        break;
                    }
                }
            }
        }

        if (next != null) {
            return next.handle(request, response);
        }

        return response;
    }
}
