package com.platonicideal.plugins.api.executors;

import java.io.IOException;
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

@Service
public class DefaultRequestExecutor implements RequestExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRequestExecutor.class);
    
    private final Supplier<CloseableHttpClient> clientSupplier;
    private final ResponseContentExtractor contentExtractor;
    private final CurlDisplayer curlDisplayer;

    @Autowired
    public DefaultRequestExecutor(Supplier<CloseableHttpClient> clientSupplier, ResponseContentExtractor contentExtractor, CurlDisplayer curlDisplayer) {
        this.clientSupplier = clientSupplier;
        this.contentExtractor = contentExtractor;
        this.curlDisplayer = curlDisplayer;
    }

    public Response execute(HttpUriRequestBase request) {
        LOG.debug("Executing {}", curlDisplayer.getCurlFor(request));
        
        try (CloseableHttpClient client = clientSupplier.get();
             CloseableHttpResponse httpResponse = client.execute(request)) {
                Response response = Response.buildFrom(httpResponse, contentExtractor);
                LOG.debug("Response was {} ({}) for {} {}", response.getCode(), response.getReason(), request.getMethod(), request.getPath());
                if(!response.isSuccessful()) {
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
}
