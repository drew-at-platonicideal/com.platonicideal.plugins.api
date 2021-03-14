package com.platonicideal.plugins.api.executors;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

import com.platonicideal.plugins.api.Response;

public class RateLimitedRequestExecutor implements RequestExecutor {

    private final RequestExecutor delegate;
    private final ExecutorService executor;

    public RateLimitedRequestExecutor(RequestExecutor delegate, ExecutorService executor) {
        this.delegate = delegate;
        this.executor = executor;
    }

    public Response execute(HttpUriRequestBase request) {
        try {
            return executor.submit(() -> delegate.execute(request)).get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new IllegalStateException(
                    "Failed during the execution of " + request.getMethod() + " " + request.getPath(), ex);
        }
    }
}
