package com.platonicideal.plugins.api.executors;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

import com.platonicideal.plugins.api.Response;

public class ThreadedRequestExecutor implements RequestExecutor {

    private final RequestExecutorPool pool;

    public ThreadedRequestExecutor(RequestExecutorPool pool) {
        this.pool = pool;
    }
    
    @Override
    public Response execute(HttpUriRequestBase request) {
        return pool.getExecutorFor(request).execute(request);
    }

}
