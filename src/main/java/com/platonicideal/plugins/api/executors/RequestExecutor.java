package com.platonicideal.plugins.api.executors;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

import com.platonicideal.plugins.api.Response;

public interface RequestExecutor {

    public Response execute(HttpUriRequestBase request);
    
}
