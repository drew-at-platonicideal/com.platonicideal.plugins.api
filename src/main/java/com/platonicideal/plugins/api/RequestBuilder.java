package com.platonicideal.plugins.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class RequestBuilder {

    private static final Charset ENCODING = StandardCharsets.UTF_8;
    
    private final String url;
    private final RequestMethod method;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final String entity;
    private final ContentType contentType;

    private RequestBuilder(String host, RequestMethod method) {
        this(host, method, new HashMap<>(), new HashMap<>(), null, null);
    }
    
    private RequestBuilder(String host, RequestMethod method, Map<String, String> headers,
            Map<String, String> parameters, String entity, ContentType contentType) {
        this.url = host;
        this.method = method;
        this.headers = headers;
        this.parameters = parameters;
        this.entity = entity;
        this.contentType = contentType;
    }

    public static RequestBuilder get(String url) {
        return new RequestBuilder(url, RequestMethod.GET);
    }
    
    public static RequestBuilder post(String url) {
        return new RequestBuilder(url, RequestMethod.POST);
    }

    public static RequestBuilder delete(String url) {
        return new RequestBuilder(url, RequestMethod.DELETE);
    }
    
    public RequestBuilder withParameter(String key, Optional<?> value) {
        if(value.isPresent()) {
            return withParameter(key, value.get().toString());
        }
        return this;
    }
    
    public RequestBuilder withParameter(String key, String value) {
        return new RequestBuilder(url, method, headers, cloneWith(parameters, key, value), entity, contentType);
    }
    
	public RequestBuilder withParameter(String key, int value) {
		return new RequestBuilder(url, method, headers, cloneWith(parameters, key, String.valueOf(value)), entity, contentType);
	}
    
    public RequestBuilder withHeader(String key, String value) {
        return new RequestBuilder(url, method, cloneWith(headers, key, value), parameters, entity, contentType);
    }
    
    private Map<String, String> cloneWith(Map<String, String> source, String key, String value) {
        Map<String, String> copy = new HashMap<>(source);
        copy.put(key, value);
        return copy;
    }
    
    public RequestBuilder withJson(String entity) {
        return withBody(entity, ContentType.APPLICATION_JSON);
    }
    
    public RequestBuilder withBody(String entity, ContentType contentType) {
        return new RequestBuilder(url, method, headers, parameters, entity, contentType);
    }

    public HttpUriRequestBase build() {
        HttpUriRequestBase request = method.request(url());
        headers.forEach((k, v) -> request.addHeader(k, v));
        if(entity != null) {
            request.setEntity(new StringEntity(entity, contentType));
        }
        return request;
    }
    
    private String url() {
        if(parameters.isEmpty()) {
            return url;
        }
        List<String> params = parameters.entrySet().stream().map((entry -> encode(entry.getKey()) + "=" + encode(entry.getValue())))
                .collect(Collectors.toList());
        return url + "?" + StringUtils.join(params, "&");
    }
    
    private String encode(String value) {
        try {
            return URLEncoder.encode(value, ENCODING.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to encode " + value + " as " + ENCODING);
        }
    }
}
