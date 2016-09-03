package io.bigpanda.exercise.tests;

import io.bigpanda.exercise.model.Event;
import io.bigpanda.exercise.processing.subscriber.EventsSubscriber;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.unit.Async;
import rx.Subscriber;

/**
 * Created by benny on 9/3/16.
 */
public class MockEventSubscriber extends Subscriber<Event> {
    private EventsSubscriber eventsSubscriber;
    private EventBus eventBus;
    private String address;
    private Async async;

    public MockEventSubscriber(EventsSubscriber eventsSubscriber, EventBus eventBus, String address, Async async) {
        this.eventsSubscriber = eventsSubscriber;
        this.eventBus = eventBus;
        this.address = address;
        this.async = async;
    }

    @Override
    public void onCompleted() {
        async.complete();
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(Event event) {
        eventsSubscriber.onNext(event);
    }
}
