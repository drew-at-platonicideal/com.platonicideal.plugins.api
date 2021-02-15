package com.platonicideal.plugins.api;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.function.Supplier;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class TrustAllClientSupplier implements Supplier<CloseableHttpClient> {

    @Override
    public CloseableHttpClient get() {
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustAllStrategy()).build();
            SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create().setSslContext(sslContext).build();
            PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create().setSSLSocketFactory(sslSocketFactory).build();
            return HttpClients.custom().setConnectionManager(connectionManager).build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new IllegalStateException("Could not create closable http client", e);
        }
    }

}
