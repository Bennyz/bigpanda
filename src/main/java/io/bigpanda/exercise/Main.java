package io.bigpanda.exercise;

import io.bigpanda.exercise.processing.ProcessingVerticle;
import io.bigpanda.exercise.processing.EventProcessor;
import io.bigpanda.exercise.processing.InputStreamProcessor;
import io.bigpanda.exercise.processing.subscriber.EventsSubscriber;
import io.bigpanda.exercise.rest.RESTVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by benny on 8/19/16
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);


    public static void main(String[] args) throws IOException {
        VertxOptions options = new VertxOptions();

        // Stop the blocking thread warning
        options.setBlockedThreadCheckInterval(1000*60*60);
        Vertx vertx = Vertx.vertx(options);

        RESTVerticle restVerticle = new RESTVerticle();
        vertx.deployVerticle(restVerticle, result -> {
            if (result.succeeded()) {
                DeploymentOptions deploymentOptions = new DeploymentOptions();
                deploymentOptions.setWorker(true);
                vertx.deployVerticle(createProcessingVerticle(vertx), deploymentOptions);
            }
        });
    }

    private static ProcessingVerticle createProcessingVerticle(Vertx vertx) {
        BufferedReader reader = new BufferedReader(startProcess("./generator-linux-amd64"));
        EventsSubscriber subscriber = new EventsSubscriber(vertx.eventBus(), "bigpanda");
        EventProcessor eventProcessor = new InputStreamProcessor(subscriber, reader);

        return new ProcessingVerticle(eventProcessor);
    }

    private static InputStreamReader startProcess(String processName) {
        ProcessBuilder processBuilder = new ProcessBuilder(processName);
        Process p = null;
        try {
            p = processBuilder.start();
        } catch (IOException e) {
            logger.error("Failed to start the process", e);
        }

        return new InputStreamReader(p.getInputStream());
    }
}
