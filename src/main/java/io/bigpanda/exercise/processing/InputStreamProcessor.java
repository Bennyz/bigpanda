package io.bigpanda.exercise.processing;

import io.bigpanda.exercise.jmx.ProccessingMBean;
import io.bigpanda.exercise.model.Event;
import io.bigpanda.exercise.processing.subscriber.EventsSubscriber;
import io.bigpanda.exercise.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.StringObservable;

import java.io.Reader;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by benny on 8/28/16.
 */
public class InputStreamProcessor implements EventProcessor, ProccessingMBean {
    private static final Logger logger = LogManager.getLogger(ProcessingVerticle.class);

    private EventsSubscriber subscriber;
    private Reader reader;
    private AtomicInteger count;

    public InputStreamProcessor(EventsSubscriber subscriber, Reader reader) {
        this.subscriber = subscriber;
        this.reader = reader;
        count = new AtomicInteger(0);
    }

    private void processInput(EventsSubscriber subscriber, Reader reader) {
        StringObservable.byLine(StringObservable.from(reader))
                .flatMap((Func1<String, Observable<Event>>) s -> {
                    logger.debug(String.format("Read event: %s", s));
                    Event e = Utils.JSONToObject(s, Event.class);

                    if (e == null) {
                        return Observable.empty();
                    } else {
                        count.set(count.incrementAndGet());

                        return Observable.just(e);
                    }
                })
                .subscribe(subscriber);
    }

    @Override
    public void process() {
        processInput(subscriber, reader);
    }

    @Override
    public int getEventCount() {
        return count.get();
    }
}
