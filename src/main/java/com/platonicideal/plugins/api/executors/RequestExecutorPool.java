package com.platonicideal.plugins.api.executors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ProtocolException;

import com.platonicideal.plugins.api.Response;

public abstract class RequestExecutorPool {

	private final int maxRequestsPerAccount;
	private final String keyHeader;
    private final DefaultRequestExecutor delegate;

    private final Map<String, RequestExecutor> pool = new HashMap<>();

    public RequestExecutorPool(DefaultRequestExecutor delegate, int maxRequestsPerAccount, String keyHeader) {
        this.delegate = delegate;
		this.maxRequestsPerAccount = maxRequestsPerAccount;
		this.keyHeader = keyHeader;
    }
    
    public RequestExecutor getExecutorFor(HttpUriRequest request) {
        try {
            Header apiKeyHeader = request.getHeader(keyHeader);
            String apiKey = apiKeyHeader != null ? apiKeyHeader.getValue() : "";
            synchronized(this) {
                if(!pool.containsKey(apiKey)) {
                    ExecutorService newFixedThreadPool = 
                            Executors.newFixedThreadPool(maxRequestsPerAccount, new NamingThreadFactory(Executors.defaultThreadFactory(), apiKey));
                    pool.put(apiKey, new ThreadedRequestExecutor(delegate, newFixedThreadPool));
                }
            }
            return pool.get(apiKey);
        } catch (ProtocolException e) {
            throw new IllegalStateException("Failed to get request executor", e);
        }
    }
    
    private static class NamingThreadFactory implements ThreadFactory {

        private final ThreadFactory delegate;
        private final String apiKey;

        private NamingThreadFactory(ThreadFactory delegate, String apiKey) {
            this.delegate = delegate;
            this.apiKey = apiKey;
        }
        
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = delegate.newThread(r);
            String threadGroupName = thread.getThreadGroup().getName();
            long threadId = thread.getId();
            String accountId = StringUtils.left(apiKey,6) + "..." + StringUtils.right(apiKey,3);
            thread.setName("Netcore" + "-" + accountId + "-" + threadGroupName + "-" + threadId);
            return thread;
        }
        
    }
    
    private static class ThreadedRequestExecutor implements RequestExecutor {

        private final RequestExecutor delegate;
        private final ExecutorService executor;

        public ThreadedRequestExecutor(RequestExecutor delegate, ExecutorService executor) {
            this.delegate = delegate;
            this.executor = executor;
        }

        public Response execute(HttpUriRequestBase request) {
            try {
                return executor.submit(() -> delegate.execute(request)).get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new IllegalStateException(
                        "Failed during the execution of " + request.getMethod() + " " + request.getPath(), ex);
            }
        }
    }

}
