package com.platonicideal.plugins.api.executors;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.platonicideal.plugins.api.Response;

@Service
public class DefaultRequestExecutor implements RequestExecutor {

    private CallableRequest.Factory callableRequestFactory;
    private ExecutorService executor;

    @Autowired
    public DefaultRequestExecutor(CallableRequest.Factory callableRequestFactory) {
        this.callableRequestFactory = callableRequestFactory;
        this.executor = Executors.newFixedThreadPool(4);
    }

    public Response execute(HttpUriRequestBase request) {
        Callable<Response> callable = callableRequestFactory.wrap(request);
        try {
            return executor.submit(callable).get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new IllegalStateException(
                    "Failed during the execution of " + request.getMethod() + " " + request.getPath(), ex);
        }
    }
}
