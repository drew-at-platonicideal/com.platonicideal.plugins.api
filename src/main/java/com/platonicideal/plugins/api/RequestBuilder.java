package com.platonicideal.plugins.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

public class RequestBuilder {

    private final String url;
    private final RequestMethod method;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;

    private RequestBuilder(String host, RequestMethod method) {
        this(host, method, new HashMap<>(), new HashMap<>());
    }
    
    private RequestBuilder(String host, RequestMethod method, Map<String, String> headers,
            Map<String, String> parameters) {
        this.url = host;
        this.method = method;
        this.headers = headers;
        this.parameters = parameters;
    }

    public static RequestBuilder get(String url) {
        return new RequestBuilder(url, RequestMethod.GET);
    }
    
    public static RequestBuilder post(String url) {
        return new RequestBuilder(url, RequestMethod.POST);
    }

    public RequestBuilder withParameter(String key, String value) {
        this.parameters.put(key, value);
        return new RequestBuilder(url, method, headers, parameters);
    }

    public HttpUriRequestBase build() {
        return method.request(url());
    }

    private String url() {
        List<String> params = parameters.entrySet().stream().map((entry -> entry.getKey() + "=" + entry.getValue()))
                .collect(Collectors.toList());
        return params.isEmpty() ? url : url + "?" + StringUtils.join(params, "&");
    }
}
