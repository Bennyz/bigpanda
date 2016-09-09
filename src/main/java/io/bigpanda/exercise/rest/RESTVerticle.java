package io.bigpanda.exercise.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bigpanda.exercise.model.Stats;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by benny on 8/26/16.
 */
public class RESTVerticle extends AbstractVerticle {
    private final static Logger logger = LogManager.getLogger(RESTVerticle.class);

    private static Stats stats = new Stats();

    @Override
    public void start() throws Exception {
        Router router = setUpRoutes(vertx);
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router::accept).listen(config().getInteger("http.port", 8080), result -> {
            logger.debug("Server started...");
            updateStats(vertx.eventBus());
        });
    }

    private Router setUpRoutes(Vertx vertx) {
        Router router = Router.router(vertx);
        Route eventsRoute = router.route(HttpMethod.GET, "/events/:eventtype");
        Route wordsRoute = router.route(HttpMethod.GET, "/events/:eventtype/words");

        eventsRoute.handler(routingContext -> {
            String eventType = routingContext.request().getParam("eventtype");
            HttpServerResponse response = routingContext.response();
            Integer eventCount = stats.getEventTypes().getOrDefault(eventType, 0);

            String result = "";

            try {
                result = new ObjectMapper().writeValueAsString(new HashMap<String, Integer>() {{
                    put(eventType, eventCount);
                }});
            } catch (JsonProcessingException e) {
                logger.error("Something bad must have happened", e);
                result = "An error occurred";
            }

            response.putHeader("content-type", "application/json").end(result);
        });

        wordsRoute.handler(routingContext -> {
            String eventType = routingContext.request().getParam("eventtype");
            HttpServerResponse response = routingContext.response();

            String result = "";

            try {
                result = new ObjectMapper().writeValueAsString(stats.getWordsCount().get(eventType));
            } catch (JsonProcessingException e) {
                logger.error("Something bad must have happened", e);
                result = "An error occurred";
            }

            response.putHeader("content-type", "application/json").end(result);
        });

        return router;
    }

    private void updateStats(EventBus eventBus) {
        logger.debug("Started updating...");
        eventBus.consumer(config().getString("eventbus.address", "bigpanda"), event -> {
            try {
                logger.debug("Incoming message: {}", event.body().toString());
                this.stats = new ObjectMapper().readValue(event.body().toString(), Stats.class);
            } catch (IOException e) {
                logger.error("Failed serialization", e);
            }
        });
    }
}
