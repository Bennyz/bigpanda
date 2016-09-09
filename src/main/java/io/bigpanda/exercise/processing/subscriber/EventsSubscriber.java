package io.bigpanda.exercise.processing.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bigpanda.exercise.model.Event;
import io.bigpanda.exercise.model.Stats;
import io.vertx.core.eventbus.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Subscriber;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by benny on 8/24/16.
 */
public class EventsSubscriber extends Subscriber<Event> {
    private final static Logger logger = LogManager.getLogger(EventsSubscriber.class);
    private final String address;

    private final Map<String, Integer> eventTypes = new HashMap<>();
    private final Map<String, Map<String, Integer>> wordsCount = new HashMap<>();

    private EventBus eventBus;

    public EventsSubscriber(EventBus eventBus, String address) {
        this.eventBus = eventBus;
        this.address = address;
    }

    @Override
    public void onNext(Event e) {
        String eventType = e.getEventType();
        eventTypes.put(e.getEventType(), eventTypes.getOrDefault(e.getEventType(), 0) + 1);

        Map<String, Integer> words = new HashMap<>();

        if (wordsCount.get(eventType) == null) {
            words.put(e.getData(), 1);
        } else {
            words.put(e.getData(), wordsCount.get(e.getEventType()).getOrDefault(e.getData(), 0) + 1);
        }
        wordsCount.put(e.getEventType(), words);

        Stats stats = new Stats(eventTypes, wordsCount);

        try {
            String message =  new ObjectMapper().writeValueAsString(stats);
            eventBus.publish(address, message);
        } catch (JsonProcessingException e1) {
            logger.error("Failed parsing JSON");
        }

        logger.debug("Added event {}", e);
    }


    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable t) {

    }
}
