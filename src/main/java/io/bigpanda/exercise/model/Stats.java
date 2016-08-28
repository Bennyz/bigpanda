package io.bigpanda.exercise.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by benny on 8/26/16.
 */
public class Stats implements Serializable {

    private Map<String, Integer> eventTypes = new HashMap<>();
    private Map<String, Map<String, Integer>> wordsCount = new HashMap<>();

    public Stats() {
    }

    public Stats(Map<String, Integer> eventTypes, Map<String, Map<String, Integer>> wordsCount) {
        this.eventTypes = eventTypes;
        this.wordsCount = wordsCount;
    }

    public Map<String, Integer> getEventTypes() {
        return eventTypes;
    }

    public Map<String, Map<String, Integer>> getWordsCount() {
        return wordsCount;
    }
}
