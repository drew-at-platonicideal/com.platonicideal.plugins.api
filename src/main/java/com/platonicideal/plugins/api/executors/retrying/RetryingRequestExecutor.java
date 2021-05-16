package com.platonicideal.plugins.api.executors.retrying;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

import com.platonicideal.plugins.api.Response;
import com.platonicideal.plugins.api.executors.RequestExecutor;

public class RetryingRequestExecutor implements RequestExecutor {

    private final RequestExecutor delegate;
    private final RetryPolicy retryPolicy;

    public RetryingRequestExecutor(RequestExecutor delegate, RetryPolicy retryPolicy) {
        this.delegate = delegate;
        this.retryPolicy = retryPolicy;
    }
    
    public Response execute(HttpUriRequestBase request) {
        int attempts = 0;
        while(true) {
            attempts++;
            Response response = delegate.execute(request);
            if(response.isSuccessful()) {
                return response;
            }
            retryPolicy.apply(attempts, response);
        }
    }
    
}
