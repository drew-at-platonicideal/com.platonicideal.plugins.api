package com.platonicideal.plugins.api.executors.retrying;

import com.platonicideal.plugins.api.Response;

public interface RetryPolicy {

    void apply(int attempts, Response response);
    
}
