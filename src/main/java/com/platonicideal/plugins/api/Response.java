package com.platonicideal.plugins.api;

import java.util.Arrays;
import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.Header;

public class Response {

    private final int code;
    private final String reason;
    private final List<Header> headers;
    private final String content;

    private Response(int code, String reason, List<Header> headers, String content) {
        this.code = code;
        this.reason = reason;
        this.headers = headers;
        this.content = content;
    }

    public static Response buildFrom(CloseableHttpResponse response, ResponseContentExtractor contentExtractor) {
        int code = response.getCode();
        String reason = response.getReasonPhrase();
        Header[] headers = response.getHeaders();
        String content = contentExtractor.apply(response);
        return new Response(code, reason, Arrays.asList(headers), content);
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public String getContent() {
        return content;
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }
}
