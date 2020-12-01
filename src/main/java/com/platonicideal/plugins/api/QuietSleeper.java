package com.platonicideal.plugins.api;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuietSleeper {

    private static final Logger LOG = LoggerFactory.getLogger(QuietMapper.class);
    
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.error("Quiet sleep was interrupted!", e);
            throw new IllegalStateException(e);
        }
    }
    
    public void sleep(int duration, TimeUnit unit) {
        sleep(TimeUnit.MILLISECONDS.convert(duration, unit));
    }
}
