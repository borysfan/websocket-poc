package com.gft.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeSleep {

    private static final Logger LOGGER = LoggerFactory.getLogger(SafeSleep.class);
    private final int millis;

    public SafeSleep(int millis) {
        this.millis = millis;
    }

    public void sleep() {
        try {
            LOGGER.info("Waiting for {} millis....", millis);
            Thread.sleep(this.millis);
        } catch (InterruptedException e) {
            LOGGER.error("Error during sleep method", e);
        }
    }
}
