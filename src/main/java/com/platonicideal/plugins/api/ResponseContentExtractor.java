package com.platonicideal.plugins.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class ResponseContentExtractor implements Function<CloseableHttpResponse, String> {

    @Override
    public String apply(CloseableHttpResponse response) {
        try {
            HttpEntity responseEntity = response.getEntity();
            InputStream content = responseEntity.getContent();
            StringWriter contentWriter = new StringWriter();
            IOUtils.copy(content, contentWriter, StandardCharsets.UTF_8);
            String s = contentWriter.toString();
            EntityUtils.consumeQuietly(responseEntity);
            return s;
        } catch(IOException ex) {
            throw new IllegalStateException("Failed while extracting response content as string", ex);
        }
    }

    
    
}
