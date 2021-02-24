package com.platonicideal.plugins.api.executors.retrying;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.platonicideal.plugins.api.Response;
import com.platonicideal.plugins.api.executors.DefaultRequestExecutor;
import com.platonicideal.plugins.api.executors.RequestExecutor;

@Service
public class RetryingRequestExecutor implements RequestExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(RetryingRequestExecutor.class);
    
    private final RequestExecutor delegate;
    private final RetryPolicy retryPolicy;

    @Autowired
    public RetryingRequestExecutor(DefaultRequestExecutor delegate, RetryPolicy retryPolicy) {
        this.delegate = delegate;
        this.retryPolicy = retryPolicy;
    }
    
    public Response execute(HttpUriRequestBase request) {
        int attempts = 0;
        while(true) {
            attempts++;
            LOG.trace("Attempt {}", attempts);
            Response response = delegate.execute(request);
            if(response.isSuccessful()) {
                return response;
            }
            retryPolicy.apply(attempts, response);
        }
    }
    
}
