package com.platonicideal.plugins.api.executors;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CurlDisplayer {

    private static final Logger LOG = LoggerFactory.getLogger(CurlDisplayer.class);
    
    public String getCurlFor(HttpUriRequestBase request) {
        String method = request.getMethod();
        String uri = getUri(request);
        String headers = StringUtils.join(Arrays.asList(request.getHeaders()).stream().map(h -> "-H \"" + h.getName() + ": " + h.getValue() + "\"").collect(Collectors.toList()), " ");
        String entityContent = getEntity(request);
        return "curl --location " + headers + " --request " + method + " '" + uri + "' " + entityContent;
    }

    private String getUri(HttpUriRequestBase request) {
        try {
            return request.getUri().toASCIIString();
        } catch (URISyntaxException e) {
            LOG.error("Error occured attempting to get uri from request", e);
            return "";
        }
    }

    private String getEntity(HttpUriRequestBase request) {
        HttpEntity entity = request.getEntity();
        if(entity == null) {
            return "";
        }
        try {
            try(InputStream is = entity.getContent()) {
                String entityContent = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
                ContentType contentType = ContentType.parse(entity.getContentType());
                request.setEntity(new StringEntity(entityContent, contentType));
                return "--data-raw '" + entityContent + "'";
            }
        } catch (IOException e) {
            LOG.error("Error occured attempting to get entity from request", e);
            return "";
        }
    }
    
}
