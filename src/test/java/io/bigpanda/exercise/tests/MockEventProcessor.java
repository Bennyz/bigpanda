package io.bigpanda.exercise.tests;

import io.bigpanda.exercise.model.Event;
import io.bigpanda.exercise.processing.EventProcessor;
import io.bigpanda.exercise.processing.subscriber.EventsSubscriber;
import io.bigpanda.exercise.utils.Utils;
import rx.Observable;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benny on 8/28/16.
 */
public class MockEventProcessor implements EventProcessor {

    private List<String> strings = new ArrayList<>();
    private EventsSubscriber subscriber;

    public MockEventProcessor(List<String> strings, EventsSubscriber subscriber) {
        this.strings = strings;
        this.subscriber = subscriber;
    }

    @Override
    public void process() {
        Observable.from(strings)
                .flatMap((Func1<String, Observable<Event>>) s -> {
                    Event e = Utils.JSONToObject(s, Event.class);

                    if (e == null) {
                        return Observable.empty();
                    } else {
                        return Observable.just(e);
                    }
                })
                .subscribe(subscriber);
    }
}
