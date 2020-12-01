package com.platonicideal.plugins.api.executors.retrying;

public interface RetryPolicy {

    void apply(int attempts);
    
}
