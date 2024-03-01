package edu.java.responseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GitHubResponse(
    String id,
    @JsonProperty("updated_at")
    OffsetDateTime updatedAt
) {}
