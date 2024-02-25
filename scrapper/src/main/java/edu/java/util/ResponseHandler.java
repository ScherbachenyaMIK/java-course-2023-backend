package edu.java.util;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.List;

@SuppressWarnings("HideUtilityClassConstructor")
public class ResponseHandler {
    public static void handleResponses(List<UserResponse> responses) {
        for (var response : responses) {
            // Getting of last update time from db
            OffsetDateTime lastUpdate = OffsetDateTime.from(OffsetTime.now());
            if (lastUpdate.isBefore(response.getTime())) {
                //logger
            }
        }
    }
}
