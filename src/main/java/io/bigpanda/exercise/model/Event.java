package io.bigpanda.exercise.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by benny on 8/21/16.
 */
public class Event implements Serializable {

    @JsonProperty(value = "event_type")
    private String eventType;
    private String data;
    private String timestamp;

    public Event() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventType='" + eventType + '\'' +
                ", data='" + data + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
