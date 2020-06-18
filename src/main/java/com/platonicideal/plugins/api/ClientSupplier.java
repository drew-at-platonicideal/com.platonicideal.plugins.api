package com.platonicideal.plugins.api;

import java.util.function.Supplier;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.stereotype.Service;

@Service
public class ClientSupplier implements Supplier<CloseableHttpClient>{

    @Override
    public CloseableHttpClient get() {
        return HttpClients.createDefault();
    }

}
