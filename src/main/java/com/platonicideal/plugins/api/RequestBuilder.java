package com.platonicideal.plugins.api;

import java.io.File;
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
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;

public class RequestBuilder {

    private static final Charset ENCODING = StandardCharsets.UTF_8;
    
    private final String url;
    private final RequestMethod method;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final Map<String, String> formValues;
    private final String entity;
    private final HttpEntity fileEntity;
    private final ContentType contentType;

    private RequestBuilder(String host, RequestMethod method) {
        this(host, method, new HashMap<>(), new HashMap<>(), new HashMap<>(), null, null, null);
    }
    
    private RequestBuilder(String host, RequestMethod method, Map<String, String> headers,
            Map<String, String> parameters, Map<String, String> formValues, String entity, HttpEntity fileEntity, 
            ContentType contentType) {
        this.url = host;
        this.method = method;
        this.headers = headers;
        this.parameters = parameters;
		this.formValues = formValues;
        this.entity = entity;
		this.fileEntity = fileEntity;
        this.contentType = contentType;
    }

    public static RequestBuilder get(String url) {
        return new RequestBuilder(url, RequestMethod.GET);
    }
    
    public static RequestBuilder post(String url) {
        return new RequestBuilder(url, RequestMethod.POST);
    }

    public static RequestBuilder put(String url) {
        return new RequestBuilder(url, RequestMethod.PUT);
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
        return new RequestBuilder(url, method, headers, cloneWith(parameters, key, value), formValues, entity, fileEntity, contentType);
    }
    
	public RequestBuilder withParameter(String key, int value) {
		return new RequestBuilder(url, method, headers, cloneWith(parameters, key, String.valueOf(value)), formValues, entity, fileEntity, contentType);
	}
	
	public RequestBuilder withFormValue(String key, String value) {
		return new RequestBuilder(url, method, headers, parameters, cloneWith(formValues, key, String.valueOf(value)), entity, fileEntity, contentType);
	}
	
	public RequestBuilder withFormValue(String key, Optional<String> value) {
		if(value.isPresent()) {
			return new RequestBuilder(url, method, headers, parameters, cloneWith(formValues, key, String.valueOf(value.get())), entity, fileEntity, contentType);
		}
		return new RequestBuilder(url, method, headers, parameters, formValues, entity, fileEntity, contentType);
	}
    
    public RequestBuilder withHeader(String key, String value) {
        return new RequestBuilder(url, method, cloneWith(headers, key, value), parameters, formValues, entity, fileEntity, contentType);
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
        return new RequestBuilder(url, method, headers, parameters, formValues, entity, fileEntity, contentType);
    }
    
    public RequestBuilder withFile(File file) {
    	HttpEntity fileEntity = MultipartEntityBuilder.create().addPart("file", new FileBody(file)).build();
    	return new RequestBuilder(url, method, headers, parameters, formValues, entity, fileEntity, ContentType.MULTIPART_FORM_DATA);
    }
    
    public HttpUriRequestBase build() {
        HttpUriRequestBase request = method.request(url());
        headers.forEach((k, v) -> request.addHeader(k, v));
        if(fileEntity != null) {
        	request.setEntity(fileEntity);
        } else if(!formValues.isEmpty()) {
        	request.setEntity(new UrlEncodedFormEntity(formValues.entrySet().stream().map(e -> new BasicNameValuePair(e.getKey(), e.getValue())).collect(Collectors.toList())));
        } else if(entity != null) {
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
