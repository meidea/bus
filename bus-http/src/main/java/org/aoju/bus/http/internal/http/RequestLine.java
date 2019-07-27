package org.aoju.bus.http.internal.http;

import org.aoju.bus.http.HttpUrl;
import org.aoju.bus.http.Request;

import java.net.HttpURLConnection;
import java.net.Proxy;

/**
 * @author aoju.org
 * @version 3.0.1
 * @group 839128
 * @since JDK 1.8
 */
public final class RequestLine {

    private RequestLine() {
    }

    /**
     * Returns the request status line, like "GET / HTTP/1.1". This is exposed to the application by
     * {@link HttpURLConnection#getHeaderFields}, so it needs to be set even if the transport is
     * HTTP/2.
     */
    public static String get(Request request, Proxy.Type proxyType) {
        StringBuilder result = new StringBuilder();
        result.append(request.method());
        result.append(' ');

        if (includeAuthorityInRequestLine(request, proxyType)) {
            result.append(request.url());
        } else {
            result.append(requestPath(request.url()));
        }

        result.append(" HTTP/1.1");
        return result.toString();
    }

    /**
     * Returns true if the request line should contain the full URL with host and port (like "GET
     * http://android.com/foo HTTP/1.1") or only the path (like "GET /foo HTTP/1.1").
     */
    private static boolean includeAuthorityInRequestLine(Request request, Proxy.Type proxyType) {
        return !request.isHttps() && proxyType == Proxy.Type.HTTP;
    }

    /**
     * Returns the path to request, like the '/' in 'GET / HTTP/1.1'. Never empty, even if the request
     * URL is. Includes the query component if it exists.
     */
    public static String requestPath(HttpUrl url) {
        String path = url.encodedPath();
        String query = url.encodedQuery();
        return query != null ? (path + '?' + query) : path;
    }
}
