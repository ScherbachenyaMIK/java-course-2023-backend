package edu.java.responseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

public record StackOverflowResponse(
    String questionId,
    OffsetDateTime lastActivityDate
) {
    StackOverflowResponse(@JsonProperty("items") List<StackOverflowItem> items) {
        this(items.getLast().questionId, items.getLast().lastActivityDate);
    }

    public record StackOverflowItem(
        @JsonProperty("question_id")
        String questionId,
        @JsonProperty("last_activity_date")
        OffsetDateTime lastActivityDate
    ) {}
}
