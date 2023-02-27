package com.platonicideal.plugins.api;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

public enum RequestMethod {

    GET {
        public HttpUriRequestBase request(String url) {
            return new HttpGet(url);
        }    
    },
    POST {
        public HttpUriRequestBase request(String url) {
            return new HttpPost(url);
        }
    },
    PUT {
        public HttpUriRequestBase request(String url) {
            return new HttpPut(url);
        }
    },
    DELETE {
        public HttpUriRequestBase request(String url) {
            return new HttpDelete(url);
        }
    }
    ;
    
    public abstract HttpUriRequestBase request(String url);
    
}
