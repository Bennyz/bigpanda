package io.bigpanda.exercise.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bigpanda.exercise.processing.EventProcessor;
import io.bigpanda.exercise.processing.ProcessingVerticle;
import io.bigpanda.exercise.processing.subscriber.EventsSubscriber;
import io.bigpanda.exercise.rest.RESTVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by benny on 8/28/16.
 */
@RunWith(VertxUnitRunner.class)
public class VerticleTest {
    private Vertx vertx;
    private int port;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();

        vertx.deployVerticle(new RESTVerticle(), new DeploymentOptions().setConfig(new JsonObject().put("http.port", 8888)), result -> {
            if (result.succeeded()) {
                vertx.deployVerticle(createProcessingVerticle(vertx), new DeploymentOptions().setWorker(true));
            } else {
                context.fail("Something happened");
            }
        });
    }

    @Test
    public void testEventTypes(TestContext context) throws InterruptedException {
        // Can be improved using future with a mock subscriber
        Thread.sleep(5000);
        Async async = context.async();

        vertx.createHttpClient().getNow(8888, "localhost", "/events/baz", result -> {
            result.handler(body -> {
                HashMap<String, Integer> content = null;
                try {
                    content = new ObjectMapper().readValue(body.toString(), HashMap.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                context.assertTrue(Integer.valueOf(2).equals(content.get("baz")));

                async.complete();
            });
        });

        vertx.createHttpClient().getNow(8888, "localhost", "/events/bar", result -> {
            result.handler(body -> {
                HashMap<String, Integer> content = null;

                try {
                    content = new ObjectMapper().readValue(body.toString(), HashMap.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                context.assertTrue(Integer.valueOf(3).equals(content.get("bar")));

                async.complete();
            });
        });

        vertx.createHttpClient().getNow(8888, "localhost", "/events/foo", result -> {
            result.handler(body -> {
                HashMap<String, Integer> content = null;

                try {
                    content = new ObjectMapper().readValue(body.toString(), HashMap.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                context.assertTrue(Integer.valueOf(0).equals(content.get("foo")));

                async.complete();
            });
        });

    }


    @Test
    public void testWords(TestContext context) throws InterruptedException {
        // Can be improved using future with a mock subscriber
        Thread.sleep(5000);
        Async async = context.async();

        vertx.createHttpClient().getNow(8888, "localhost", "/events/bar/words", result -> {
            result.handler(body -> {
                HashMap<String, Integer> content = null;
                try {
                    content = new ObjectMapper().readValue(body.toString(), HashMap.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                context.assertTrue(Integer.valueOf(3).equals(content.get("ipsum")));

                async.complete();
            });
        });

        vertx.createHttpClient().getNow(8888, "localhost", "/events/baz/words", result -> {
            result.handler(body -> {
                HashMap<String, Integer> content = null;
                try {
                    content = new ObjectMapper().readValue(body.toString(), HashMap.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                context.assertTrue(Integer.valueOf(2).equals(content.get("amet")));

                async.complete();
            });
        });
    }

    private ProcessingVerticle createProcessingVerticle(Vertx vertx) {
        final EventsSubscriber eventsSubscriber = new EventsSubscriber(vertx.eventBus(), "bigpanda");
        final EventProcessor eventProcessor = new MockEventProcessor(createJSONList(), eventsSubscriber);

        return new ProcessingVerticle(eventProcessor);
    }

    private List<String> createJSONList() {
        return new ArrayList<String>() {{
            add("{ \"event_type\": \"bar\", \"data\": \"ipsum\", \"timestamp\": 1472411792 }");
            add("{ \"event_type\": \"bar\", \"data\": \"ipsum\", \"timestamp\": 1472411793 }");
            add("{ \"event_type\": \"bar\", \"data\": \"ipsum\", \"timestamp\": 1472411794 }");
            add("{ \"event_type\": \"baz\", \"data\": \"amet\", \"timestamp\": 1472411795 }");
            add("{ \"event_type\": \"baz\", \"data\": \"amet\", \"timestamp\": 1472411796 }");
        }};
    }
}
