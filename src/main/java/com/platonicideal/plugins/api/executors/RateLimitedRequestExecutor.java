package com.platonicideal.plugins.api.executors;

import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platonicideal.plugins.api.Response;

public final class RateLimitedRequestExecutor implements RequestExecutor {

	private static final Logger LOG = LoggerFactory.getLogger(RateLimitedRequestExecutor.class);
	
	private final RequestExecutor delegate;
	private final TimedSemaphore timedSemaphore;

	public RateLimitedRequestExecutor(RequestExecutor delegate, TimedSemaphore timedSemaphore) {
		this.delegate = delegate;
		this.timedSemaphore = timedSemaphore;
	}
	
	@Override
	public Response execute(HttpUriRequestBase request) {
		do {
			try {
				LOG.info("Acquiring semaphore");
				timedSemaphore.acquire();
				LOG.info("Semaphore acquired");
				Response response = delegate.execute(request);
				if(response.isSuccessful()) {
					return response;
				} else {
					LOG.info("Request not successful; response was {} ({}): {} - retrying", response.getCode(), response.getReason(), response.getContent());
				}
			} catch (InterruptedException ex) {
				throw new IllegalStateException(
						"Failed during the execution of " + request.getMethod() + " " + request.getPath(), ex);
			}
		} while(true);
	}

}
