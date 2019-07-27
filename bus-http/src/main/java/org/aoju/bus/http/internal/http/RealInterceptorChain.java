package org.aoju.bus.http.internal.http;

import org.aoju.bus.http.*;
import org.aoju.bus.http.internal.Internal;
import org.aoju.bus.http.internal.connection.RealConnection;
import org.aoju.bus.http.internal.connection.StreamAllocation;
import org.aoju.bus.http.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A concrete interceptor chain that carries the entire interceptor chain: all application
 * interceptors, the httpClient core, all network interceptors, and finally the network caller.
 *
 * @author aoju.org
 * @version 3.0.1
 * @group 839128
 * @since JDK 1.8
 */
public final class RealInterceptorChain implements Interceptor.Chain {

    private final List<Interceptor> interceptors;
    private final StreamAllocation streamAllocation;
    private final HttpCodec httpCodec;
    private final RealConnection connection;
    private final int index;
    private final Request request;
    private final Call call;
    private final EventListener eventListener;
    private final int connectTimeout;
    private final int readTimeout;
    private final int writeTimeout;
    private int calls;

    public RealInterceptorChain(List<Interceptor> interceptors, StreamAllocation streamAllocation,
                                HttpCodec httpCodec, RealConnection connection, int index, Request request, Call call,
                                EventListener eventListener, int connectTimeout, int readTimeout, int writeTimeout) {
        this.interceptors = interceptors;
        this.connection = connection;
        this.streamAllocation = streamAllocation;
        this.httpCodec = httpCodec;
        this.index = index;
        this.request = request;
        this.call = call;
        this.eventListener = eventListener;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
    }

    @Override
    public Connection connection() {
        return connection;
    }

    @Override
    public int connectTimeoutMillis() {
        return connectTimeout;
    }

    @Override
    public Interceptor.Chain withConnectTimeout(int timeout, TimeUnit unit) {
        int millis = Internal.checkDuration("timeout", timeout, unit);
        return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index,
                request, call, eventListener, millis, readTimeout, writeTimeout);
    }

    @Override
    public int readTimeoutMillis() {
        return readTimeout;
    }

    @Override
    public Interceptor.Chain withReadTimeout(int timeout, TimeUnit unit) {
        int millis = Internal.checkDuration("timeout", timeout, unit);
        return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index,
                request, call, eventListener, connectTimeout, millis, writeTimeout);
    }

    @Override
    public int writeTimeoutMillis() {
        return writeTimeout;
    }

    @Override
    public Interceptor.Chain withWriteTimeout(int timeout, TimeUnit unit) {
        int millis = Internal.checkDuration("timeout", timeout, unit);
        return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index,
                request, call, eventListener, connectTimeout, readTimeout, millis);
    }

    public StreamAllocation streamAllocation() {
        return streamAllocation;
    }

    public HttpCodec httpStream() {
        return httpCodec;
    }

    @Override
    public Call call() {
        return call;
    }

    public EventListener eventListener() {
        return eventListener;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Response proceed(Request request) throws IOException {
        return proceed(request, streamAllocation, httpCodec, connection);
    }

    public Response proceed(Request request, StreamAllocation streamAllocation, HttpCodec httpCodec,
                            RealConnection connection) throws IOException {
        if (index >= interceptors.size()) throw new AssertionError();

        calls++;

        // If we already have a stream, confirm that the incoming request will use it.
        if (this.httpCodec != null && !this.connection.supportsUrl(request.url())) {
            throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
                    + " must retain the same host and port");
        }

        // If we already have a stream, confirm that this is the only call to chain.proceed().
        if (this.httpCodec != null && calls > 1) {
            throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
                    + " must call proceed() exactly once");
        }

        // Call the next interceptor in the chain.
        RealInterceptorChain next = new RealInterceptorChain(interceptors, streamAllocation, httpCodec,
                connection, index + 1, request, call, eventListener, connectTimeout, readTimeout,
                writeTimeout);
        Interceptor interceptor = interceptors.get(index);
        Response response = interceptor.intercept(next);

        // Confirm that the next interceptor made its required call to chain.proceed().
        if (httpCodec != null && index + 1 < interceptors.size() && next.calls != 1) {
            throw new IllegalStateException("network interceptor " + interceptor
                    + " must call proceed() exactly once");
        }

        // Confirm that the intercepted response isn't null.
        if (response == null) {
            throw new NullPointerException("interceptor " + interceptor + " returned null");
        }

        if (response.body() == null) {
            throw new IllegalStateException(
                    "interceptor " + interceptor + " returned a response with no body");
        }

        return response;
    }
}
