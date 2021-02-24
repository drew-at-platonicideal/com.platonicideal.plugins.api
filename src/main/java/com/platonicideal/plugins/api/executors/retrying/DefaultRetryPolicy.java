package com.platonicideal.plugins.api.executors.retrying;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.platonicideal.plugins.api.Response;
import com.platonicideal.utils.QuietSleeper;

@Service
@Primary
public class DefaultRetryPolicy implements RetryPolicy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRetryPolicy.class);
    
    private static final int MAX = 5;
    
    private final QuietSleeper sleeper;

    public DefaultRetryPolicy(QuietSleeper sleeper) {
        this.sleeper = sleeper;
    }
    
    public void apply(int attempts, Response ignored) {
        LOG.debug("Retry attempt {} of {}", attempts, MAX);
        if(attempts < MAX) {
            LOG.info("Trying again in {}m", attempts);
            sleeper.sleep(attempts, TimeUnit.MINUTES);
        } else {
            throw new IllegalArgumentException("Maximum number of retries exceeded");
        }
    }
    
}
