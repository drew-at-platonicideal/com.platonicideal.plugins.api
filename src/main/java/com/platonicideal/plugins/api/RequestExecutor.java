package com.platonicideal.plugins.api;

import java.io.IOException;
import java.util.function.Function;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestExecutor {

    private final ClientSupplier clientSupplier;
    private final ResponseContentExtractor contentExtractor;

    @Autowired
    public RequestExecutor(ClientSupplier clientSupplier, ResponseContentExtractor contentExtractor) {
        this.clientSupplier = clientSupplier;
        this.contentExtractor = contentExtractor;
    }

    public <ResponseType> ResponseType execute(HttpUriRequestBase request, Function<String, ResponseType> mappingFunction) {
        try (CloseableHttpClient client = clientSupplier.get()) {
            try (CloseableHttpResponse response = client.execute(request)) {
                return contentExtractor.andThen(mappingFunction).apply(response);
            }
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Failed during the execution of " + request.getMethod() + " " + request.getPath(), ex);
        }
    }

}
