package com.platonicideal.plugins.api.executors;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.platonicideal.plugins.api.Response;
import com.platonicideal.plugins.api.ResponseContentExtractor;

public class CallableRequest implements Callable<Response> {

    private static final Logger LOG = LoggerFactory.getLogger(CallableRequest.class);
    
    private final HttpUriRequestBase request;
    private final CurlDisplayer curlDisplayer;
    private final Supplier<CloseableHttpClient> clientSupplier;
    private final ResponseContentExtractor contentExtractor;
    
    private CallableRequest(HttpUriRequestBase request, CurlDisplayer curlDisplayer, Supplier<CloseableHttpClient> clientSupplier, ResponseContentExtractor contentExtractor) {
        this.request = request;
        this.curlDisplayer = curlDisplayer;
        this.clientSupplier = clientSupplier;
        this.contentExtractor = contentExtractor;
    }
    
    @Override
    public Response call() throws Exception {
        LOG.debug("Executing {}", curlDisplayer.getCurlFor(request));
        long start = System.currentTimeMillis();
        try (CloseableHttpClient client = clientSupplier.get();
                CloseableHttpResponse httpResponse = client.execute(request)) {
            long duration = System.currentTimeMillis() - start;
            Response response = Response.buildFrom(httpResponse, contentExtractor);
            LOG.debug("Response was {} ({}) for {} {} in {}ms", response.getCode(), response.getReason(),
                    request.getMethod(), request.getPath(), duration);
            if (!response.isSuccessful()) {
                LOG.error("Error Response body: {}", response.getContent());
            } else {
                LOG.trace("Response content {}", response.getContent());
            }
            return response;
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Failed during the execution of " + request.getMethod() + " " + request.getPath(), ex);
        }
    }
    
    @Service
    public static class Factory {
        private final CurlDisplayer curlDisplayer;
        private final Supplier<CloseableHttpClient> clientSupplier;
        private final ResponseContentExtractor contentExtractor;

        @Autowired
        public Factory(CurlDisplayer curlDisplayer, Supplier<CloseableHttpClient> clientSupplier, ResponseContentExtractor contentExtractor) {
            this.curlDisplayer = curlDisplayer;
            this.clientSupplier = clientSupplier;
            this.contentExtractor = contentExtractor;
        }
        
        public Callable<Response> wrap(HttpUriRequestBase request) {
            return new CallableRequest(request, curlDisplayer, clientSupplier, contentExtractor);
        }
    }

}
