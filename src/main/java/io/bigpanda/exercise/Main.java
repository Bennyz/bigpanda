package io.bigpanda.exercise;

import io.bigpanda.exercise.jmx.ProccessingMBean;
import io.bigpanda.exercise.processing.EventProcessor;
import io.bigpanda.exercise.processing.InputStreamProcessor;
import io.bigpanda.exercise.processing.ProcessingVerticle;
import io.bigpanda.exercise.processing.subscriber.EventsSubscriber;
import io.bigpanda.exercise.rest.RESTVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.management.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

/**
 * Created by benny on 8/19/16
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final String EXECUTABLE = "./generator-linux-amd64";
    private static final String EVENT_BUS_ADDRESS = "bigpanda";

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
                vertx.deployVerticle(createProcessingVerticle(vertx, EXECUTABLE, EVENT_BUS_ADDRESS), deploymentOptions);
            }
        });
    }

    private static void setUpJMX(InputStreamProcessor processor) throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        StandardMBean mbean = new StandardMBean(processor, ProccessingMBean.class);
        ObjectName name = new ObjectName("io.bigpanda:type=EventCount");
        mBeanServer.registerMBean(mbean, name);
    }

    private static ProcessingVerticle createProcessingVerticle(Vertx vertx, String path, String eventBusAddress) {
        BufferedReader reader = new BufferedReader(startProcess(path));
        EventsSubscriber subscriber = new EventsSubscriber(vertx.eventBus(), eventBusAddress);
        EventProcessor eventProcessor = new InputStreamProcessor(subscriber, reader);

        try {
            setUpJMX((InputStreamProcessor)eventProcessor);
        } catch (Exception e) {
            logger.error("Something bad happened");
        }

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
