package io.bigpanda.exercise.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by benny on 8/28/16.
 */
public class Utils {

    private static final Logger logger = LogManager.getLogger(Utils.class);


    public static <T> T JSONToObject(String s, Class<T> T) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(s, T);
        } catch (IOException e) {
            logger.error("Bad JSON");
        }

        return null;
    }
}
