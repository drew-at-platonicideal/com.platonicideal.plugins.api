package com.platonicideal.plugins.api;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

public enum RequestMethod {

    GET {
        public HttpUriRequestBase request(String url) {
            return new HttpGet(url);
        }    
    },
    ;
    
    public abstract HttpUriRequestBase request(String url);
    
}
