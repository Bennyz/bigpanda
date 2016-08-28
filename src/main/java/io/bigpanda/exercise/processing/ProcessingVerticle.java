package io.bigpanda.exercise.processing;

import io.vertx.core.AbstractVerticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by benny on 8/26/16.
 */
public class ProcessingVerticle extends AbstractVerticle {
    private static final Logger logger = LogManager.getLogger(ProcessingVerticle.class);

    private EventProcessor eventProcessor;

    public ProcessingVerticle(EventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @Override
    public void start() throws IOException {
        logger.debug("Processing" +
                " started...");
        eventProcessor.process();

    }
}


